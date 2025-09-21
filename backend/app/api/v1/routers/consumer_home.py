"""
Router para la página de inicio de consumidores
"""

from fastapi import APIRouter, Request
from fastapi.responses import HTMLResponse

router = APIRouter(prefix="/consumer", tags=["Consumer Home"])

@router.get("/", response_class=HTMLResponse)
async def consumer_home(request: Request):
    """
    Página de inicio para consumidores
    """
    
    html_content = """
    <!DOCTYPE html>
    <html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>🌱 Trazabilidad de Productos</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }
            
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
            }
            
            .container {
                background: white;
                border-radius: 20px;
                box-shadow: 0 20px 40px rgba(0,0,0,0.1);
                padding: 40px;
                max-width: 500px;
                width: 90%;
                text-align: center;
            }
            
            .logo {
                font-size: 4em;
                margin-bottom: 20px;
            }
            
            h1 {
                color: #333;
                margin-bottom: 10px;
                font-size: 2.5em;
                font-weight: 300;
            }
            
            .subtitle {
                color: #666;
                margin-bottom: 40px;
                font-size: 1.1em;
                line-height: 1.6;
            }
            
            .search-box {
                margin-bottom: 30px;
            }
            
            .search-input {
                width: 100%;
                padding: 15px 20px;
                border: 2px solid #e0e0e0;
                border-radius: 50px;
                font-size: 1.1em;
                outline: none;
                transition: border-color 0.3s;
            }
            
            .search-input:focus {
                border-color: #4CAF50;
            }
            
            .search-button {
                background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
                color: white;
                border: none;
                padding: 15px 30px;
                border-radius: 50px;
                font-size: 1.1em;
                cursor: pointer;
                margin-top: 15px;
                transition: transform 0.2s;
            }
            
            .search-button:hover {
                transform: translateY(-2px);
            }
            
            .features {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
                gap: 20px;
                margin-top: 40px;
            }
            
            .feature {
                padding: 20px;
                background: #f8f9fa;
                border-radius: 15px;
                text-align: center;
            }
            
            .feature-icon {
                font-size: 2.5em;
                margin-bottom: 10px;
            }
            
            .feature-title {
                font-weight: bold;
                color: #333;
                margin-bottom: 5px;
            }
            
            .feature-desc {
                color: #666;
                font-size: 0.9em;
            }
            
            .footer {
                margin-top: 40px;
                color: #666;
                font-size: 0.9em;
            }
            
        </style>
    </head>
    <body>
        <div class="container">
            <div class="logo">🌱</div>
            <h1>Trazabilidad de Productos</h1>
            <p class="subtitle">
                Descubre la historia completa de tus productos alimentarios.<br>
                Desde el productor hasta tu mesa.
            </p>
            
            <div class="search-box">
                <input type="text" class="search-input" id="productId" placeholder="Ingresa el ID del producto (ej: 1, 2, 3...)" />
                <button class="search-button" onclick="searchProduct()">🔍 Ver Trazabilidad</button>
            </div>
            
            
            <div class="features">
                <div class="feature">
                    <div class="feature-icon">🌾</div>
                    <div class="feature-title">Origen</div>
                    <div class="feature-desc">Conoce al productor</div>
                </div>
                <div class="feature">
                    <div class="feature-icon">🚚</div>
                    <div class="feature-title">Transporte</div>
                    <div class="feature-desc">Ruta y condiciones</div>
                </div>
                <div class="feature">
                    <div class="feature-icon">✅</div>
                    <div class="feature-title">Calidad</div>
                    <div class="feature-desc">Verificaciones</div>
                </div>
                <div class="feature">
                    <div class="feature-icon">🔒</div>
                    <div class="feature-title">Seguridad</div>
                    <div class="feature-desc">Blockchain</div>
                </div>
            </div>
            
            <div class="footer">
                <p>🔒 Información verificada por tecnología blockchain</p>
                <p>🌱 Productos ecológicos y sostenibles</p>
            </div>
        </div>
        
        <script>
            function searchProduct() {
                const productId = document.getElementById('productId').value;
                if (productId) {
                    window.location.href = `/api/v1/consumer/products/${productId}/trace`;
                } else {
                    alert('Por favor, ingresa un ID de producto válido');
                }
            }
            
            
            // Permitir búsqueda con Enter
            document.getElementById('productId').addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    searchProduct();
                }
            });
        </script>
    </body>
    </html>
    """
    
    return HTMLResponse(content=html_content)
