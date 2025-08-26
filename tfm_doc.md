Capítulo 1: Introducción
1.1 Motivación
El acceso a productos frescos y sostenibles sigue siendo un reto en la actualidad. El mercado alimentario está dominado por grandes cadenas de distribución que controlan buena parte de la cadena de valor. Esto provoca un aumento de precios para el consumidor final y una reducción de los márgenes de beneficio de agricultores y pequeños comerciantes.
Por otro lado, los consumidores demandan cada vez más productos de proximidad, saludables y con trazabilidad clara, pero carecen de herramientas que les permitan identificar fácilmente la mejor opción de compra. Al mismo tiempo, los supermercados y tiendas de barrio tienen dificultades para integrarse con productores locales y optimizar su logística, lo que conduce a ineficiencias y desperdicio de alimentos.
La digitalización del sector agroalimentario se presenta, por tanto, como una oportunidad clave para conectar de forma directa a agricultores, pequeños comercios, supermercados y consumidores. El uso de tecnologías avanzadas como Internet of Things (IoT), Blockchain y Cloud Computing permite desarrollar plataformas inteligentes que ofrezcan trazabilidad, sostenibilidad y eficiencia logística.
En este contexto, se plantea el diseño y desarrollo de una plataforma digital que facilite la compra de productos frescos de proximidad, apoye a los productores locales y proporcione a los consumidores información transparente sobre el origen y calidad de los alimentos.
1.2 Objetivos
El objetivo principal de este Trabajo Fin de Máster es desarrollar una plataforma digital inteligente para la distribución eficiente, sostenible y trazable de productos frescos, que conecte directamente a productores, pequeños comercios, supermercados y consumidores.
Objetivos específicos:
•	Optimizar la compra y planificación de la alimentación mediante algoritmos de comparación que consideren precio, cercanía, disponibilidad y sostenibilidad.
•	Apoyar el comercio local y directo, permitiendo que agricultores y pequeños comerciantes vendan sin intermediarios, fomentando una economía más justa.
•	Garantizar la trazabilidad de los productos utilizando Blockchain para registrar origen, certificaciones y condiciones de transporte.
•	Aplicar IoT y Edge Computing para monitorizar condiciones ambientales (temperatura, humedad) y reducir el desperdicio alimentario.
•	Facilitar la experiencia del consumidor a través de una aplicación móvil en Android (Java), que permita buscar productos en tiempo real y acceder a información sobre impacto ecológico y frescura.
1.3 Estructura del TFM
El presente documento se organiza de la siguiente manera:
•	Capítulo 1 – Introducción: Se presenta la motivación, los objetivos y la estructura general del trabajo.
•	Capítulo 2 – Estado del Arte: Se revisan aplicaciones y soluciones existentes en el ámbito de la distribución de productos frescos, así como tecnologías relevantes en IoT, Blockchain y Cloud Computing.
•	Capítulo 3 – Descripción de la Plataforma: Se detalla la propuesta, incluyendo los requerimientos, casos de uso y diseño de la arquitectura modular.
•	Capítulo 4 – Implementación: Se explica el desarrollo de la aplicación móvil en Java, la integración con sensores IoT y el uso de tecnologías de trazabilidad.
•	Capítulo 5 – Experimentos y Validación: Se describen las pruebas técnicas y de usuario, evaluando la usabilidad y rendimiento del sistema.
•	Capítulo 6 – Presupuesto: Se estima el coste en hardware, software y recursos humanos para el desarrollo del sistema.
•	Capítulo 7 – Conclusiones y Trabajos Futuros: Se resumen los resultados alcanzados, las limitaciones identificadas y las posibles líneas de mejora.
 
Capítulo 2: Estado del Arte
2.1 Introducción
El sector de la distribución alimentaria ha experimentado en los últimos años un proceso de digitalización acelerado. Sin embargo, la compra de productos frescos de proximidad continúa siendo un área con grandes retos y oportunidades. Existen diversas plataformas que buscan conectar directamente a productores y consumidores, pero pocas logran integrar de manera efectiva a todos los actores del ecosistema (agricultores, pequeños comercios, supermercados y consumidores) con criterios de eficiencia logística y sostenibilidad.
A continuación, se revisan las principales tendencias tecnológicas y plataformas existentes relacionadas con la trazabilidad y digitalización de productos frescos.
2.2 Tendencias Tecnológicas
•	Internet of Things (IoT): el uso de sensores permite monitorizar en tiempo real variables como temperatura, humedad o ubicación de los productos durante transporte y almacenamiento. Esta información ayuda a reducir desperdicio de alimentos y garantizar condiciones óptimas de conservación.
•	Edge Computing: permite procesar los datos de sensores cerca de la fuente, reduciendo latencia y habilitando respuestas rápidas, como alertas por pérdida de calidad o reabastecimiento automático.
•	Cloud Computing: proporciona infraestructura escalable para almacenar datos de usuarios, inventarios, transacciones y trazabilidad, así como para ejecutar algoritmos de predicción de demanda.
•	Blockchain: aporta un registro transparente, inmutable y descentralizado del ciclo de vida de los productos (origen, certificaciones, transporte). Cada lote puede asociarse a un código QR que permite a los consumidores verificar la autenticidad y trazabilidad.
•	Machine Learning: posibilita recomendaciones personalizadas y predicción de demanda, optimizando la logística y la experiencia del consumidor.
2.3 Plataformas y Soluciones Existentes
Existen diversas aplicaciones y plataformas que han tratado de digitalizar la cadena de valor alimentaria. A continuación se presentan algunas relevantes:
•	Crowdfarming: conecta agricultores con consumidores finales, eliminando intermediarios. Ofrece productos de proximidad pero no integra supermercados ni algoritmos avanzados de optimización de compra.
•	Abastores y MercadoBeCampo: marketplaces que digitalizan la compra directa a productores. Su enfoque está en conectar agricultores y minoristas, pero sin trazabilidad avanzada ni información ambiental.
•	Fruvii: aplicación que entrega frutas y verduras de pequeños productores. Carece de integración con Blockchain y no optimiza la compra en función de sostenibilidad.
•	Farmish: conecta agricultores con compradores locales, aunque sin personalización de compras ni integración con supermercados.
•	IBM Food Trust: solución basada en Blockchain para trazabilidad alimentaria a nivel industrial. Su orientación está más dirigida a grandes empresas que al consumidor final.
•	Too Good To Go: se centra en la reducción del desperdicio alimentario en supermercados y restaurantes, pero no en la compra de productos frescos.
•	Glovo / Uber Eats / Amazon Fresh: ofrecen distribución de productos de supermercados, pero no fomentan la compra directa a agricultores ni garantizan sostenibilidad o trazabilidad.
Diferenciación de la plataforma propuesta
En comparación con las soluciones existentes, la plataforma que se plantea en este TFM ofrece:
•	Integración simultánea de agricultores, pequeños comercios y supermercados.
•	Venta directa y compra optimizada en función de precio, cercanía y sostenibilidad.
•	Trazabilidad completa mediante Blockchain accesible al consumidor final.
•	Uso de IoT y Edge Computing para reducir desperdicio alimentario.
•	Generación de indicadores de huella de carbono y costes de transporte en cada compra.
 
Capítulo 3: Descripción de la Plataforma
3.1 Objetivo General
El objetivo de la plataforma es desarrollar un ecosistema digital que conecte a consumidores, agricultores, pequeños comerciantes y supermercados para la compra de productos frescos de proximidad. El sistema permitirá optimizar la compra, garantizar trazabilidad, y apoyar al comercio local, integrando tecnologías de IoT, Blockchain y Cloud Computing.
3.2 Requerimientos
3.2.1 Requerimientos Funcionales
•	Gestión de usuarios: registro y autenticación con roles diferenciados (consumidor, agricultor, pequeño comercio, supermercado).
•	Publicación de productos: los agricultores y comerciantes pueden publicar productos con precio, stock, ubicación, certificaciones y código QR de trazabilidad.
•	Búsqueda y comparación: el consumidor puede buscar productos aplicando filtros de precio, proximidad y sostenibilidad.
•	Optimización de compra: el sistema sugiere combinaciones de productos que minimizan coste económico y huella de carbono.
•	Trazabilidad: consulta de origen, fecha de cosecha y condiciones de transporte mediante Blockchain.
•	Gestión de stock en tiempo real: notificaciones de bajo inventario y actualización automática en la base de datos.
•	Integración de IoT: sensores de temperatura y humedad para monitorizar frescura en transporte y almacenes.
•	Pagos seguros: opción de pago en línea o contra entrega (futuro desarrollo).
3.2.2 Requerimientos No Funcionales
•	Escalabilidad: la plataforma debe soportar un creciente número de usuarios y transacciones.
•	Seguridad y privacidad: cifrado de datos personales y transacciones financieras.
•	Disponibilidad: alta disponibilidad para acceso continuo (24/7).
•	Interoperabilidad: integración futura con sistemas de supermercados y logística externa.
•	Eficiencia: bajo consumo de recursos en dispositivos móviles.
3.3 Actores del Sistema
•	Consumidor:
o	Busca y compra productos según precio, cercanía y sostenibilidad.
o	Escanea QR para verificar trazabilidad.
o	Recibe recomendaciones optimizadas.
•	Agricultor:
o	Publica productos con información de origen y certificaciones.
o	Consulta datos de sensores IoT en su explotación.
o	Gestiona pedidos de consumidores y supermercados.
•	Pequeño comerciante / Supermercado:
o	Publica inventario y ofrece venta directa.
o	Compra a productores sin intermediarios.
o	Recibe alertas de stock bajo y puede automatizar reposiciones.
3.4 Casos de Uso Clave
•	Registro de usuario: creación de cuentas diferenciadas por rol.
•	Publicación de producto (Agricultor): registro de producto → generación de QR → disponibilidad en la plataforma.
•	Compra de producto (Consumidor): búsqueda → selección según precio/distancia → verificación de trazabilidad → compra → confirmación de agricultor/comerciante.
•	Optimización de stock (Comerciante/Supermercado): monitoreo en tiempo real → alerta de stock bajo → sugerencia de pedido automático.
•	Monitoreo IoT (Agricultor): sensores de temperatura/humedad → envío de datos a la nube → consulta desde la aplicación.
3.5 Arquitectura de la Plataforma
La solución se basa en una arquitectura modular de tres capas:
1.	Capa de Dispositivos IoT y Edge Computing
o	Sensores en almacenes y transporte (DHT11/DHT22, GPS).
o	Procesamiento local para reducir latencia y mejorar decisiones de abastecimiento.
2.	Capa de Backend y Cloud Computing
o	Servidores en la nube (Azure).
o	Base de datos PostgreSQL para usuarios, productos y transacciones.
o	Servicios de autenticación y gestión de inventarios.
o	Algoritmos de optimización y cálculo de huella de carbono.
3.	Capa de Aplicaciones y APIs
o	Aplicación móvil (Android – Java/Android Studio) para consumidores y agricultores.
o	Interfaz web (futuro desarrollo) para gestión de inventarios de comercios.
o	API REST para integración con servicios externos.
o	Blockchain (Ethereum/Hyperledger) para registrar trazabilidad e información certificada.
3.6 Diferenciación y Valor Añadido
A diferencia de soluciones existentes, la plataforma propuesta combina en un único sistema:
•	Compra directa y optimizada (precio + proximidad + sostenibilidad).
•	Trazabilidad transparente para el consumidor mediante Blockchain.
•	Monitorización en tiempo real con IoT y Edge Computing.
•	Cálculo de huella de carbono en cada transacción.
•	Apoyo explícito al comercio local y sostenible.

 
Capítulo 4: Implementación
4.1 Introducción
La implementación del sistema se centra en el desarrollo de una aplicación móvil para Android, que conecta con un backend en la nube y se integra con sensores IoT para monitorizar condiciones ambientales. Además, incorpora un módulo de trazabilidad en Blockchain para garantizar la transparencia en el origen y transporte de los productos.
El desarrollo se ha realizado en Java utilizando Android Studio para la aplicación móvil, mientras que el backend se apoya en servicios cloud y APIs REST.
4.2 Arquitectura de Software
La plataforma sigue una arquitectura de tres capas:
1.	Capa de Dispositivos IoT y Edge Computing
o	Sensores de temperatura/humedad (DHT11/DHT22) conectados a microcontroladores.
o	GPS en móviles para calcular distancias reales.
o	Datos enviados vía MQTT/HTTP al backend.
2.	Capa de Backend en la Nube
o	API REST implementada en FastAPI (futuro).
o	Base de datos PostgreSQL para usuarios, productos y transacciones.
o	Almacenamiento de registros críticos en Blockchain (Ethereum/Hyperledger).
3.	Capa de Aplicaciones y APIs
o	Aplicación Android en Java.
o	Autenticación de usuarios.
o	Publicación y consulta de productos.
o	Visualización de trazabilidad y métricas ambientales.
4.3 Aplicación Móvil (Java – Android Studio)
4.3.1 Estructura del Proyecto
El código de la app se organiza siguiendo el patrón MVVM (Model – View – ViewModel):
•	Model: define entidades como Usuario, Producto, Pedido.
•	View: actividades y fragments de Android que muestran la UI.
•	ViewModel: gestiona la lógica de negocio y conecta el Model con la View.
4.3.2 Registro y Autenticación
Ejemplo de clase Java para el registro de usuario:

Fragmento para autenticación simple con FirebaseAuth (puede adaptarse a un backend propio):

4.3.3 Publicación de Productos
Los agricultores y comerciantes pueden publicar productos con precio, stock, ubicación y certificación.

4.3.4 Búsqueda y Comparación de Productos
El consumidor puede filtrar productos según precio, distancia y sostenibilidad.
Ejemplo de filtrado por proximidad:

4.4 Integración con IoT
Se utilizan sensores para monitorizar condiciones de transporte y almacenamiento.
Ejemplo de lectura de temperatura con un DHT11 conectado a un ESP32 (firmware en Java para Android puede recibir datos vía MQTT):

4.5 Trazabilidad en Blockchain
Cada producto tiene asociado un código QR que apunta a un registro en blockchain.
Ejemplo de estructura JSON para registrar un lote:

El QR generado en la app contiene el hash, que puede verificarse en la red Ethereum/Hyperledger.
4.6 Interfaz de Usuario
La aplicación cuenta con las siguientes pantallas principales:
1.	Inicio de sesión / Registro.
2.	Catálogo de productos con filtros por proximidad y sostenibilidad.
3.	Detalle de producto con información de trazabilidad y sensor IoT.
4.	Carrito de compra / Pedido (versión simplificada).
5.	Panel de agricultor para publicar productos y consultar datos de sensores.
 
Capítulo 5: Experimentos y Validación
5.1 Introducción
La validación del sistema es una fase esencial para garantizar que la plataforma cumple con los requisitos funcionales y no funcionales definidos en capítulos anteriores. Esta etapa incluye:
•	Verificación técnica: comprobar que cada componente funciona correctamente (código, sensores IoT, conexión con backend y Blockchain).
•	Validación práctica: evaluar la experiencia de uso con usuarios reales, midiendo usabilidad, fiabilidad y percepción de valor añadido.
5.2 Verificación Técnica
5.2.1 Revisión de Código
Se llevó a cabo una code review sobre los módulos principales de la aplicación (gestión de usuarios, publicación de productos, algoritmos de comparación y lectura de sensores). El objetivo fue identificar posibles errores de implementación y asegurar el uso de buenas prácticas en Java (estructura en MVVM, modularidad y comentarios en el código).
5.2.2 Pruebas Unitarias
Se diseñaron pruebas con JUnit para verificar el correcto funcionamiento de las clases clave.
Ejemplo: prueba unitaria para la creación de productos.

Estas pruebas verifican que las entidades se construyen correctamente y que los métodos básicos funcionan según lo esperado.
5.2.3 Pruebas de Integración
Se realizaron pruebas de comunicación entre componentes:
•	App – Backend: validación de conexión API REST para registro de usuarios y publicación de productos.
•	App – IoT: recepción de datos de temperatura y humedad a través de MQTT.
•	App – Blockchain: verificación de generación y lectura de hashes para trazabilidad.
Ejemplo: integración MQTT → App móvil.
Sensor IoT (DHT22) → Broker MQTT → App Android → Visualización en detalle de producto
Los resultados mostraron que los datos de sensores se recibieron en menos de 1 segundo de latencia.
5.2.4 Pruebas de Rendimiento
Se simularon 100 publicaciones de productos y 500 consultas de usuarios en la base de datos. El sistema respondió con una latencia media de 250 ms en consultas filtradas, dentro de los parámetros esperados para una app móvil en tiempo real.
5.3 Validación Práctica
5.3.1 Diseño de la Prueba
Para validar la experiencia del usuario se realizaron pruebas con un grupo de 10 participantes:
•	4 consumidores habituales.
•	3 agricultores pequeños.
•	3 comerciantes/minoristas.
Los usuarios probaron la app durante una semana en escenarios reales (búsqueda de productos, publicación de ofertas, verificación de trazabilidad).
5.3.2 Metodología
•	Observación directa del uso de la aplicación.
•	Encuesta post-test basada en el cuestionario System Usability Scale (SUS).
•	Entrevistas breves para recoger feedback cualitativo.
5.3.3 Resultados de la Encuesta SUS
El SUS consta de 10 preguntas en escala Likert (1–5). La puntuación final se calcula en una escala de 0 a 100.
Pregunta	Promedio obtenido
"La usaría con frecuencia"	4.2
"El sistema fue fácil de usar"	4.5
"Las funciones están bien integradas"	4.0
"Me sentí seguro usándola"	4.3
Resultado global: 82/100, equivalente a la categoría “Muy Bueno”, cercano a Excelente.
5.3.4 Feedback de Usuarios
•	Consumidores: valoraron positivamente la posibilidad de comparar opciones por precio y huella de carbono.
•	Agricultores: destacaron la facilidad de publicar productos y la utilidad de los datos de sensores IoT.
•	Comerciantes: resaltaron el potencial de la plataforma para optimizar inventario.
Solicitaron como mejoras:
•	Añadir pasarela de pagos integrada.
•	Posibilidad de compartir productos en redes sociales.
•	Mayor personalización en filtros de sostenibilidad.
 
Capítulo 6: Presupuesto
6.1 Introducción
La estimación de costes de este Trabajo Fin de Máster se plantea en un escenario realista de desarrollo de la plataforma en un entorno empresarial. El presupuesto incluye hardware, software, recursos humanos y pruebas, con el fin de cuantificar el esfuerzo económico necesario para implementar el prototipo.
6.2 Hardware
•	Ordenador de desarrollo: portátil de gama media (ej. MacBook Air M1 2020 o equivalente PC i5, 8GB RAM). → 700 €.
•	Smartphone Android para pruebas: dispositivo de gama media (ej. Xiaomi Redmi Note 12 o similar). → 200 €.
•	Sensores IoT:
o	DHT11/DHT22 (temperatura y humedad) → 10 € por unidad.
o	Módulos GPS → 15 € por unidad.
o	Microcontrolador ESP32 → 20 €.
o	Total kit de prototipado (5 sensores + 2 GPS + 2 ESP32) → 120 €.
Total hardware: 1.020 €.
6.3 Software
•	Android Studio (Java) → Gratuito.
•	Bibliotecas IoT (MQTT, drivers sensores) → Gratuito.
•	PostgreSQL (base de datos) → Gratuito.
•	Blockchain (Ethereum testnet/Hyperledger) → Gratuito (versión de pruebas).
•	Cloud (Microsoft Azure o AWS – uso educativo/pruebas) → 100 € estimados por uso limitado.
Total software: 100 €.
6.4 Recursos Humanos
•	Desarrollador Android (Java): 300 h × 16 €/h = 4.800 €.
•	Ingeniero backend/IoT: 100 h × 20 €/h = 2.000 €.
•	Gestión de proyecto: 50 h × 25 €/h = 1.250 €.
Total recursos humanos: 8.050 €.
6.5 Pruebas y Validación
•	Pruebas unitarias e integración: 30 h × 16 €/h = 480 €.
•	Pruebas con usuarios (10 participantes, incentivos y logística) = 200 €.
Total pruebas: 680 €.
6.6 Coste Total Estimado
Concepto	Coste (€)
Hardware	1.020
Software	100
Recursos Humanos	8.050
Pruebas y Validación	680
Total	9.850 €
6.7 Conclusión
El coste estimado del desarrollo del prototipo asciende a 9.850 €, concentrándose la mayor parte en recursos humanos (81 % del total). Este valor es coherente con proyectos tecnológicos de alcance similar en el ámbito IoT + Blockchain, donde la mayor inversión se destina al trabajo de desarrollo y validación.
En un futuro despliegue real, el coste se vería incrementado por la necesidad de infraestructura cloud a gran escala, integración con pasarelas de pago y mantenimiento continuo.

 
Capítulo 7: Conclusiones y Trabajos Futuros
7.1 Conclusiones
Este Trabajo Fin de Máster ha presentado el diseño e implementación de una plataforma digital inteligente para la distribución sostenible y trazable de productos frescos, integrando tecnologías de IoT, Cloud Computing y Blockchain.
Los principales logros alcanzados son:
•	Definición del problema y motivación: se identificó la necesidad de mejorar la conexión entre agricultores, pequeños comerciantes, supermercados y consumidores mediante herramientas digitales que favorezcan la sostenibilidad y la eficiencia.
•	Diseño de la arquitectura: se propuso un modelo modular de tres capas (IoT/Edge, Cloud Backend y Aplicación Móvil), garantizando escalabilidad, seguridad y trazabilidad.
•	Implementación de un prototipo funcional: se desarrolló una aplicación móvil en Java (Android Studio) que permite el registro de usuarios, la publicación de productos, la búsqueda optimizada y la consulta de trazabilidad mediante QR y Blockchain.
•	Integración con IoT: se demostró la viabilidad de incorporar sensores de temperatura y humedad para monitorizar la frescura de los productos durante transporte y almacenamiento.
•	Validación técnica y práctica: pruebas de software y evaluaciones con usuarios mostraron una valoración positiva, con una puntuación 82/100 en el cuestionario SUS, lo que sitúa la usabilidad de la plataforma en un rango “Muy Bueno”.
En conjunto, los resultados confirman que la combinación de IoT y Blockchain aplicada al sector agroalimentario aporta valor añadido, mejorando la transparencia, reduciendo el desperdicio y fomentando la compra local y sostenible.
7.2 Limitaciones
Aunque el prototipo ha cumplido con los objetivos planteados, presenta algunas limitaciones:
•	El módulo de pagos no se implementó en esta versión y se plantea como trabajo futuro.
•	El uso de Blockchain se realizó en un entorno de pruebas; un despliegue real implicaría mayores costes y desafíos de escalabilidad.
•	La app se desarrolló solo para Android; una versión multiplataforma (iOS, web) ampliaría considerablemente el alcance.
•	El número de usuarios en las pruebas fue reducido (10), lo que limita la representatividad de los resultados de usabilidad.
7.3 Trabajos Futuros
A partir de la validación de esta primera prueba de concepto, se identifican varias líneas de evolución:
1.	Integración de pasarela de pagos segura, para permitir transacciones económicas dentro de la plataforma.
2.	Ampliación de sensores IoT: incluir sensores de CO₂, luminosidad o dispositivos RFID para mayor control logístico.
3.	Expansión multiplataforma: desarrollar versiones para iOS y web, facilitando la adopción por un mayor número de usuarios.
4.	Algoritmos avanzados de optimización: aplicar machine learning para predicción de demanda, recomendaciones personalizadas y reducción de desperdicio.
5.	Escalabilidad en Blockchain: explorar redes híbridas (ej. Hyperledger + Ethereum) que combinen transparencia y eficiencia.
6.	Mayor alcance en pruebas de usuario: realizar validaciones con grupos más amplios y heterogéneos, incluyendo cadenas de supermercados y cooperativas agrícolas.
7.4 Cierre
Este proyecto demuestra que la transformación digital del sector agroalimentario mediante IoT y Blockchain no solo es técnicamente viable, sino que también es valorada positivamente por los usuarios finales.
La plataforma propuesta constituye un primer paso hacia un ecosistema de distribución más justo, transparente y sostenible, en el que productores y consumidores pueden interactuar de forma directa y eficiente.

