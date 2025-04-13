# App Clash of Clans  
  
### Especificaciones de desarrollo
  
- SDK 21 Eclipse version Temurin 21.0.2  
- JavaFX Scene Builder 23.0.1

### Uso de la API  
  
Usa la API del juego de móvil [Clash of clans](https://developer.clashofclans.com/#/), para poder usarla hay que registrarse.  
Para que la conexión con la API sea correcta hay que crear una API key y especificar que IPs externas se van a conectar a la API.  

Para utilizar la funcionalidad de búsqueda de TOP 10 de cada país en el Clash Royale, hay que registrarse en este [link](https://developer.clashroyale.com/#/) de la misma manera en la que se hizo en la API del Clash of Clans.  

  
### Variable de entorno  
  
La aplicación busca la API key en el archivo /env/ApiKey.java.  
En ese mismo directorio está el archivo de ejemplo ApiKey_Example.java, copiar las API key al archivo de ejemplo y renombrarlo a ApiKey.   
TOKEN es la API key del Clash of Clans y CR_TOKEN la del Clash Royale.  
  
### Servicio webflux (Opción top clan) 
  
La opción top clan funciona con un servicio Webflux que esta en este [repositorio](https://github.com/MMAndresC/PS-AA2-webflux).
