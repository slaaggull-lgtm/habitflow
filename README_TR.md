# HabitFlow 🌱 🍵
<div align="center">

<img src="https://img.shields.io/badge/version-1.0.0-7BA05B?style=for-the-badge&labelColor=EDE9E1"/>
<img src="https://img.shields.io/badge/Java-17+-5D8A6B?style=for-the-badge&logo=openjdk&logoColor=white&labelColor=EDE9E1"/>
<img src="https://img.shields.io/badge/Spring_Boot-3.2-A8C5A0?style=for-the-badge&logo=springboot&logoColor=white&labelColor=EDE9E1"/>
<img src="https://img.shields.io/badge/SQLite-3-7BA05B?style=for-the-badge&logo=sqlite&logoColor=white&labelColor=EDE9E1"/>
<img src="https://img.shields.io/badge/Render-Deploy-C4D9BC?style=for-the-badge&labelColor=EDE9E1"/>

<br/><br/>

```
  🌱  H a b i t F l o w
  ─────────────────────
  her seferinde bir alışkanlıkla,her gün kendinizi geliştirin
```

**Modern ve matcha esintili bir alışkanlık takipçisi — Java Spring Boot web uygulaması.🌿🌿**  
Günlük alışkanlıkları takip edin · rozetler kazanın · gelişiminizi görselleştirin · Render.com üzerinde canlıda.

🌱 🍵[[Canlı gelişim sürecini izlemek için buraya tıklayın](https://habitflow-4ins.onrender.com)] (Not: Proje şu an aktif olarak geliştirilmektedir. Sadece tamamlanan modüller canlıya yansımaktadır.)

</div>

---

##  💚Özellikler

- **🟢Günlük İşaretleme** — Alışkanlıkları tek tıkla tamamlandı olarak işaretleyin
- **🟢Seri  Takibi** — Güncel ve en uzun seri sayaçları
- **🟢10 Başarı Rozeti** — İlk Adım'dan 🌱 Yüzyıllık Kulüp'e 🏆 kadar uzanan ödüller
- **🟢İstatistikler** — 30 günlük aktivite grafiği ve alışkanlık dağılımı
- **🟢Akıllı Kategoriler** — Sağlık, Fitness, Öğrenme, Farkındalık , Verimlilik ve daha fazlası
- **🟢Özelleştirilebilir** — Alışkanlık başına isim, açıklama, hedef günler ve özel renk tanımlama
- **🟢Matcha Tasarımı** —Krem arka planlar, adaçayı yeşilleri ve tamamen mobil uyumlu arayüz
- **🟢Java Backend Altyapıs** — Spring Boot + Thymeleaf + JPA + SQLite

---

## ❇️ Render ile Canlıya Alın (Kurulum Gerektirmez)

### ✅Adım 1 — Github'a Yükleme

1. [github.com] adresine gidin (https://github.com) → **New repository** → adını `HabitFlow` yapın → **Create**
2. [GitHub Desktop] uygulamasını indirin (https://desktop.github.com/) (no terminal needed)
3. GitHub Desktop'ı açın → **Add Existing Repository** → bilgisayarınızdaki zipten çıkarılmış klasörü seçin
4. **Publish Repository** butonuna tıklayın →GitHub hesabınızı seçin →Yayınlayın

### ✅Adım 2 — Render üzerinde Canlıya Alma

1.  [render.com] adresine gidin. (https://render.com) → Ücretsiz kaydolun (GitHub ile giriş yapın)
2.  **New +** butonuna tıklayın → **Web Service** seçeneğini seçin
3.  GitHub hesabınızı bağlayın →  `HabitFlow` reposunu seçin
4.  Render, ayarları proje içindeki render.yaml dosyasından otomatik olarak algılayacaktır
5.  **Create Web Service** butonuna tıklayın → yaklaşık ~3 dakika bekleyin.
6. Uygulamanız `https://habitflow-xxxx.onrender.com` adresinde canlıda! 🎉

> **Not:** Ücretsiz planda uygulama 15 dakika boyunca istek almazsa uyku moduna geçer ve ilk açılışta uyanması yaklaşık 30 saniye sürer. Kesintisiz erişim için ücretli plana ($7/ay) geçiş yapabilirsiniz.

---

## 🔋 Teknoloji Yığını

| Teknoloji | Versiyon| Kullanım Amacı|
|------------|---------|---------|
| Java | 17 |Programlama Dili |
| Spring Boot | 3.2 | Web Çatısı (Framework) |
| Thymeleaf | 3.x | HTML Şablon Motoru|
| Spring Data JPA | 3.x | Veritabanı ORM Katmanı |
| SQLite | 3.45 | Gömülü Veritabanı|
| Maven | 3.8+ | Proje ve Bağımlılık Yönetimi |
| Render.com | — | Bulut Sunucu / Barındırma |

---

## 🟩 Bulut Sunucu / Barındırma

```
habitflow/
├── src/main/java/com/habitflow/
│   ├── HabitFlowApplication.java        # Uygulama başlangıç noktası
│   ├── model/
│   │   ├── Habit.java                   # Alışkanlık varlığı + Kategori enum yapısı
│   │   ├── HabitLog.java                # Günlük tamamlanma günlükleri (log)
│   │   └── Badge.java                   # Başarı rozeti varlığı
│   ├── repository/
│   │   ├── HabitRepository.java
│   │   ├── HabitLogRepository.java
│   │   └── BadgeRepository.java
│   ├── service/
│   │   ├── HabitService.java            # İş mantığı ve rozet ödüllendirme sistemi
│   │   └── DataSeeder.java              # Başlangıçta varsayılan verileri yükler
│   └── controller/
│       └── HabitController.java         # Tüm web yönlendirmeleri (route)
├── src/main/resources/
│   ├── templates/
│   │   ├── fragments/
│   │   │   ├── layout.html              # Kenar çubuğu ve ana iskelet
│   │   │   └── habit-card.html          # Yeniden kullanılabilir kart bileşeni
│   │   └── pages/
│   │       ├── dashboard.html
│   │       ├── habits.html
│   │       ├── habit-form.html
│   │       ├── statistics.html
│   │       └── badges.html
│   ├── static/css/styles.css            # Matcha teması CSS kodları
│   └── application.properties
├── render.yaml                          # Render otomatik kurulum ayarları
└── pom.xml
```

---

## 💚 Rozetler

| Rozet | Gereksinim |
|-------|-------------|
| 🌱 İlk Adım | İlk alışkanlığı tamamla|
| 🌿 Hafta Savaşçısı | 7 günlük seri yakala |
| 🍃 İki Haftalık Akış | 14 günlük seri yakala |
| 🌳 Aylık Usta |30 günlük seri yakala |
| 🏆 Yüzyıllık Kulüp | Toplamda 100 tamamlamaya ulaş|
| 🌸 Çoklu Alışkanlık | 5 aktif alışkanlığa sahip ol |
| ⭐ Kusursuz Hafta | 30 gün boyunca farkındalık egzersizi yap |
| 🍵 Matcha Zen | 30 days mindfulness |
| 🌅 Erkenci Kuş | Sabah saatlerinde 7 gün üst üste istikrar sağla |
| 🔥 Geri Dönüş Rüzgarı | Bir aradan sonra alışkanlığa yeniden başla |

---

## 📝Lisans

MIT Lisansı —detaylar için [LICENSE](LISANS) dosyasına ulaşabilirsin.

---

<div align="center">



*Küçük alışkanlıklar. Büyük değişimler. Her geçen gün.. 🍵🌱*

[README_md]( https://github.com/slaaggull-lgtm/habitflow/blob/main/README.md)
[🇬🇧 For English README click here](README.md)

</div>

---
