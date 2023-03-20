# Fitwalk - Bases3 Project


## Integrantes:  
-David Anteliz  <br>
-Jesus Molina  <br>
-Álvaro Betancourt <br>

## Objetivo de la aplicación:  
Fitwalk es una aplicación móvil que tiene como propósito llevar un registro de las caminatas que hace una persona, recopilando distintos datos que le puedan presentar de manera agradable y útil al usuario, mientras a su vez la aplicación le hace un plan alimenticio en base a sus requerimientos y objetivos; desglosando las funciones de la aplicación quedaría de esta manera.

- Habrá un apartado dedicado a los datos estadísticos de las caminatas del usuario en forma de dashboard, que contará con filtros para que pueda comparar su progreso. <br>
- Otro apartado será dedicado al plan alimenticio del usuario, desde allí podrá revisar las comidas que la aplicación le recomienda, eliminar o agregar alimentos que consumió a lo largo del día y habrá distintos medidores que le indicarán los nutrientes que ha comido a lo largo del día, con sus respectivas calorías. <br>
- En una actividad el usuario podrá modificar su perfil, cuadrar sus objetivos con respecto a las caminatas y dar información acerca de sus preferencias alimenticias para darle un plan adecuado a sus necesidades. <br>
- La aplicación tendrá un tipo de blog dedicado a los trayectos sugeridos, estos tendrán distintos tipos de niveles de acuerdo a la dificultad de los mismos, los usuarios podrán probar dichos trayectos, compartir fotos y una calificación de estos, de igual manera podrán sugerir trayectos que hayan hecho y publicarlos en dicho apartado. <br>
- Los usuarios podrán agregar a conocidos a una lista de amigos, estas personas les aparecerán en un mapa si están utilizando la aplicación, de modo que puedan formar caminatas juntos. <br>
- Por medio de la ubicación del usuario se le recomendaran rutas que puedan ser de su interés y le queden cerca, también se le mostrará los amigos que tengan cerca. <br>
- La aplicación creará un enlace web donde habrá un registro de las caminatas hechas por el usuario junto a datos pertinentes, más las comidas que ha tenido dicho usuario, esto con el propósito de compartirlo con un tercero, por ejemplo con su medico de confianza. <br>

## Servicios y hardware utilizados:    
- Google Maps: Se usará para poder acceder a los mapas necesarios para el trayecto  
- Android Studio: Se usará para poder desarrollar la aplicación   
- Power bi: Se utilizara para desarrollar la plantilla de los dashboard con los datos relevantes que generan la caminata de un usuario   
- Firebase: La utilizaremos como herramienta para el desarrollo de la aplicación, desde allí manejaremos aspectos como pueden ser de analitica de datos.  
- Hardware del Dispositivo móvil:  
- Acelerómetro: se usará para calcular la velocidad de la caminata  
- GPS: Se usará para crear el trayecto, saber en dónde está el inicio y final del trayecto y ubicar al  usuario.  
- Cámaras: se usarán para sacar una foto a los puntos de interés de la aplicación como el inicio y el final del trayecto.  
- Pantallas móviles: se usarán como dispositivos de entrada para la navegación y manejo de la aplicación, así como seleccionar las opciones deseadas.  
- memoria interna: Se usa para poder descargar la aplicación y los datos necesarios para mantenerla funcional  
- Sensores de movimiento: Para reconocer lo movimientos del usuario.  

## Preguntas de QUICES

- ¿Qué diferencia hay entre DPs, SPs y PX?

  •PX: usan los píxeles de la pantalla.

  •DPs (density-independent pixels): tienen el mismo tamaño sin tener en cuenta que tantos pixeles hay en la pantalla

  •SPs (scalable pixels): es para el tamaño del texto

- ¿Qué es un text view? ¿Cuáles son los 2 atributos (fuera del texto) que son obligatorios y que aplican también para cualquier objeto dentro de cualquier layout?
  Alto y ancho

- ¿Cuál es la diferencia entre LinearLayout y ConstrainLayout?
  LinearLayout organiza los elementos en una dirección lineal (horizontal o vertical). ConstraintLayout utiliza restricciones para posicionar y ajustar los   elementos en una vista.

- ¿En dónde se deben guardar las cadenas de texto que se quieren mostrar en una aplicación Android?
  En el archivo strings

- ¿Cómo se llama el componente que permite enviar mensajes temporales en pantalla?
  Toast

- ¿Qué clase agrupa los identificadores de los recursos en Android?
  La clase: R

- ¿Cuándo se llama a onDestroy()?
  Cuando la actividad termina o está siendo destruida y a punto de ser eliminada de la memoria.

- ¿Cómo se llama el objeto que permite lanzar una nueva actividad desde Kotlin?
  Intent

- ¿Para que se usa el FrameLayout?
  Para tener objetos transparentes que se pueden poner uno encima de otro, ej una imagen encima de otro objeto

- ¿Para qué sirve un adapter?
  Para interpretar los datos que se necesite mostrar una pantalla

- ¿Qué clase contiene los identificadores de las vistas definidas en los layouts?
  La clase: R

- ¿Qué atributo se debe definir para cargar los valores de un spinner?
  Un array
