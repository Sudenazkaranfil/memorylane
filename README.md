# MemoryLane — Backend

Spring Boot ile geliştirilmiş RESTful API. Kullanıcı kimlik doğrulama, ajanda yönetimi, canvas editör verisi, fotoğraf yükleme ve harita entegrasyonu sağlar.

## Teknolojiler

- Java 25 + Spring Boot 4.1
- PostgreSQL + Spring Data JPA / Hibernate
- JWT (JSON Web Token) kimlik doğrulama
- Cloudinary (fotoğraf depolama)
- OpenStreetMap / Nominatim (geocoding)
- Lombok, Spring Security, Spring Validation

## Özellikler

- Kullanıcı kayıt ve giriş (JWT)
- Ajanda oluşturma, güncelleme, silme (public/private)
- Canvas editör verisi (drag-drop elementler, çizimler) JSON olarak saklama
- Fotoğraf yükleme (Cloudinary entegrasyonu)
- Konum tabanlı entry'ler (lat/lng geocoding)
- Public ajanda keşfet ve arama
- Kullanıcı profil yönetimi

## Kurulum

### Gereksinimler
- Java 21+
- PostgreSQL 17
- Maven

### Adımlar

1. Repoyu klonla
```bash
git clone https://github.com/Sudenazkaranfil/memorylane.git
cd memorylane
```

2. PostgreSQL'de veritabanı oluştur
```sql
CREATE DATABASE memorylane;
```

3. `src/main/resources/application.properties` dosyası oluştur
```properties
spring.application.name=memorylane
spring.datasource.url=jdbc:postgresql://localhost:5432/memorylane
spring.datasource.username=postgres
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
jwt.secret=YOUR_JWT_SECRET
jwt.expiration=86400000
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
```

4. Uygulamayı başlat
```bash
mvn spring-boot:run
```

## API Endpoints

### Auth
| Method | URL | Açıklama |
|--------|-----|----------|
| POST | /auth/register | Kayıt ol |
| POST | /auth/login | Giriş yap |
| GET | /auth/profile | Profil bilgisi |
| PUT | /auth/profile | Profil güncelle |

### Journals
| Method | URL | Açıklama |
|--------|-----|----------|
| GET | /journals | Ajandalarım |
| POST | /journals | Ajanda oluştur |
| PUT | /journals/{id} | Ajanda güncelle |
| DELETE | /journals/{id} | Ajanda sil |
| GET | /journals/public | Public ajandalar |

### Entries
| Method | URL | Açıklama |
|--------|-----|----------|
| GET | /journals/{id}/entries | Sayfalar |
| POST | /journals/{id}/entries | Sayfa oluştur |
| PUT | /journals/{id}/entries/{entryId} | Sayfa güncelle |
| DELETE | /journals/{id}/entries/{entryId} | Sayfa sil |

### Photos
| Method | URL | Açıklama |
|--------|-----|----------|
| POST | /entries/{id}/photos | Fotoğraf yükle |
