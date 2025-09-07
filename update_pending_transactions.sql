-- Script para actualizar transacciones con estado 'pending' a 'in_progress'
-- Ejecutar este script en la base de datos para resolver el error del enum

-- Actualizar todas las transacciones con estado 'pending' a 'in_progress'
UPDATE transactions 
SET status = 'in_progress' 
WHERE status = 'pending';

-- Verificar que no queden transacciones con estado 'pending'
SELECT COUNT(*) as pending_count 
FROM transactions 
WHERE status = 'pending';

-- Mostrar el resumen de estados actuales
SELECT status, COUNT(*) as count 
FROM transactions 
GROUP BY status 
ORDER BY status;
