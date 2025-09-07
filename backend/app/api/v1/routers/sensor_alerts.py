from datetime import datetime, timedelta
import os
from fastapi import APIRouter, Depends, HTTPException, Query
from sqlalchemy.orm import Session
from typing import List, Optional

from backend.app import schemas, database, models
from backend.app.database import get_db
from backend.app.api.v1.routers.dependencies import get_current_user

router = APIRouter(prefix="/sensor-alerts", tags=["Sensor Alerts"])

@router.get("/", response_model=List[schemas.SensorAlertRead])
def get_sensor_alerts(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
    status: Optional[str] = Query(None),
    severity: Optional[str] = Query(None),
    hours: int = Query(24, le=168)  # Máximo 1 semana
):
    """Obtener alertas de sensores del farmer actual."""
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden ver alertas")
    
    # Obtener alertas de las últimas N horas
    since = datetime.utcnow() - timedelta(hours=hours)
    
    query = db.query(models.SensorAlert).join(models.Sensor).join(models.SensorZone).filter(
        models.SensorZone.farmer_id == current_user.id,
        models.SensorAlert.created_at >= since
    )
    
    if status:
        query = query.filter(models.SensorAlert.status == status)
    
    if severity:
        query = query.filter(models.SensorAlert.severity == severity)
    
    alerts = query.order_by(models.SensorAlert.created_at.desc()).all()
    return alerts

@router.get("/active", response_model=List[schemas.SensorAlertRead])
def get_active_alerts(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Obtener alertas activas del farmer actual."""
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden ver alertas")
    
    alerts = db.query(models.SensorAlert).join(models.Sensor).join(models.SensorZone).filter(
        models.SensorZone.farmer_id == current_user.id,
        models.SensorAlert.status == models.AlertStatusEnum.active
    ).order_by(models.SensorAlert.created_at.desc()).all()
    
    return alerts

@router.get("/public/active", response_model=List[schemas.SensorAlertRead])
def get_active_alerts_public(
    db: Session = Depends(get_db),
):
    """Obtener alertas activas sin autenticación en modo demo.

    Requiere DEMO_MODE=true en variables de entorno.
    """
    if os.getenv("DEMO_MODE", "false").lower() != "true":
        raise HTTPException(status_code=403, detail="Endpoint deshabilitado")
    alerts = db.query(models.SensorAlert).filter(
        models.SensorAlert.status == models.AlertStatusEnum.active
    ).order_by(models.SensorAlert.created_at.desc()).all()
    return alerts

@router.put("/{alert_id}/acknowledge", response_model=schemas.SensorAlertRead)
def acknowledge_alert(
    alert_id: int,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Reconocer una alerta."""
    alert = db.query(models.SensorAlert).join(models.Sensor).join(models.SensorZone).filter(
        models.SensorAlert.id == alert_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not alert:
        raise HTTPException(status_code=404, detail="Alerta no encontrada")
    
    alert.status = models.AlertStatusEnum.acknowledged
    alert.acknowledged_at = datetime.utcnow()
    alert.acknowledged_by = current_user.id
    
    db.commit()
    db.refresh(alert)
    return alert

@router.put("/{alert_id}/resolve", response_model=schemas.SensorAlertRead)
def resolve_alert(
    alert_id: int,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Resolver una alerta."""
    alert = db.query(models.SensorAlert).join(models.Sensor).join(models.SensorZone).filter(
        models.SensorAlert.id == alert_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not alert:
        raise HTTPException(status_code=404, detail="Alerta no encontrada")
    
    alert.status = models.AlertStatusEnum.resolved
    alert.resolved_at = datetime.utcnow()
    
    db.commit()
    db.refresh(alert)
    return alert

@router.put("/{alert_id}/dismiss", response_model=schemas.SensorAlertRead)
def dismiss_alert(
    alert_id: int,
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user)
):
    """Descartar una alerta."""
    alert = db.query(models.SensorAlert).join(models.Sensor).join(models.SensorZone).filter(
        models.SensorAlert.id == alert_id,
        models.SensorZone.farmer_id == current_user.id
    ).first()
    
    if not alert:
        raise HTTPException(status_code=404, detail="Alerta no encontrada")
    
    alert.status = models.AlertStatusEnum.dismissed
    
    db.commit()
    db.refresh(alert)
    return alert

@router.get("/stats")
def get_alert_stats(
    db: Session = Depends(get_db),
    current_user: models.User = Depends(get_current_user),
    days: int = Query(7, le=30)  # Máximo 30 días
):
    """Obtener estadísticas de alertas."""
    if current_user.role != models.UserRoleEnum.farmer:
        raise HTTPException(status_code=403, detail="Solo los farmers pueden ver estadísticas")
    
    since = datetime.utcnow() - timedelta(days=days)
    
    # Obtener alertas del farmer
    alerts = db.query(models.SensorAlert).join(models.Sensor).join(models.SensorZone).filter(
        models.SensorZone.farmer_id == current_user.id,
        models.SensorAlert.created_at >= since
    ).all()
    
    # Calcular estadísticas
    total_alerts = len(alerts)
    active_alerts = len([a for a in alerts if a.status == models.AlertStatusEnum.active])
    acknowledged_alerts = len([a for a in alerts if a.status == models.AlertStatusEnum.acknowledged])
    resolved_alerts = len([a for a in alerts if a.status == models.AlertStatusEnum.resolved])
    
    # Alertas por severidad
    severity_stats = {}
    for alert in alerts:
        severity = alert.severity
        if severity not in severity_stats:
            severity_stats[severity] = 0
        severity_stats[severity] += 1
    
    # Alertas por tipo
    type_stats = {}
    for alert in alerts:
        alert_type = alert.alert_type.value
        if alert_type not in type_stats:
            type_stats[alert_type] = 0
        type_stats[alert_type] += 1
    
    return {
        "period_days": days,
        "total_alerts": total_alerts,
        "active_alerts": active_alerts,
        "acknowledged_alerts": acknowledged_alerts,
        "resolved_alerts": resolved_alerts,
        "severity_breakdown": severity_stats,
        "type_breakdown": type_stats
    }
