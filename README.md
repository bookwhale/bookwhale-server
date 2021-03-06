<p align="center">
  <img src="https://user-images.githubusercontent.com/55661631/145812918-77ca9ff1-d682-4514-940c-48bf547b594c.png" width="50%" height="45%"></a>
</p>

## π Intro

### μλΉμ€ μκ°
- μμ½ : μ€κ³ μ± κ±°λ νλ«νΌ
![img.png](images/img.png)
- κΈ°λ₯μ μ λ¦¬νλ©΄ μλμ κ°μ΅λλ€.
  - μ±κ±°λ νμμ λ±λ‘ν  μ μμ΅λλ€. (OAuthλ₯Ό ν΅ν μ¬μ©μ μ λ³΄ μμ²­)
  - Naver μ± κ²μ APIλ₯Ό ν΅ν΄ μ±μ λ³΄λ₯Ό μ‘°νν  μ μμ΅λλ€.
  - λ‘κ·ΈμΈν μ¬μ©μλ μ€κ³ μ± νλ§€κΈμ λ±λ‘, μμ , μ­μ ν  μ μμ΅λλ€.
  - νλ§€κΈλ‘ λ±λ‘λ μ€κ³ μ±μ μ‘°νν  μ μμ΅λλ€. (νλ§€κΈ λͺ©λ‘ μ‘°ν)
  - μνλ μ€κ³ μ±μ νλ§€νλ νλ§€κΈμ νμΈνλ©΄ νλ§€μ ~ κ΅¬λ§€μκ° μ±νμ ν΅ν΄ κ±°λκ° μ§νλ©λλ€.
  
## β‘οΈ Skills
κΈ°μ  μ€ν
### νκ²½ μμ½
- JDK 11
- Spring Boot 2.5.2
- Spring Data JPA
  - MySQL
  - MongoDB (μ±ν λ΄μ© μ μ₯)
  - querydsl
- Web Socket
  - stomp-websocket
- Rest-Assured (μΈμνμ€νΈ κ΅¬μ±)
- Spring REST Docs
  - spring-restdocs-asciidoctor
- Commons-lang3, Guava
- Jasypt (property μνΈν)
- AWS μΈνλΌ νμ© (EC2, S3, RDB)
- Firebase-admin (push μλ¦Ό κ΅¬ν)
- intellij-java-google-style μ μ©
- java-jwt

### νλ‘μ νΈ λΉλ
- Gradle Multi Module Setting
  ```java
  rootProject.name = 'bookwhale-server'
  include 'api'
  include 'chat'
  include 'core'
  ```
- properties λ΄ λ―Όκ°μ λ³΄ μνΈν
  - jasyptλ₯Ό ν΅ν μνΈν μ²λ¦¬ (https://github.com/ulisesbocchio/jasypt-spring-boot)

### λ‘κ·Έ μ€μ 
- Springboot κΈ°λ³Έ μ€μ λ logback νμ©
  - ConsoleAppender
  - RollingFileAppender
  - SlackAppender (com.github.maricn.logback.SlackAppender)
    - AsyncAppender νμ©

## π  Project Architecture

μμ€ν κ΅¬μ±λ
![λλ©μΈ λͺ¨λΈ](images/domainModel.png)

## π₯ Demo

λ°λͺ¨

## π  Members

|            [gentledot](https://github.com/GentleDot)             |            [highright96](https://github.com/highright96)             |  
| :----------------------------------------------------------: | :----------------------------------------------------------: 
| <img src="https://user-images.githubusercontent.com/55661631/145813189-67f4b845-a9f7-490e-837b-5f7997305f27.png" width=200px alt="_"/> | <img src="https://user-images.githubusercontent.com/55661631/141674899-d7496769-6736-47ee-a0cc-2c631520790a.jpg" width=200px alt="_"/>
|                         λ°±μλ                         |                        λ°±μλ                         |  

