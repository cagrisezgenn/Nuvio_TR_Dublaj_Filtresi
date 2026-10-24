# v0.3 — CSRepoManager kaynak analizi ve Nuvio entegrasyonu

## İncelenen özgün kaynak
- Kullanıcı tarafından yüklenen: `CSRepoManager_User.apk`.
- APK içi: `assets/central_repos.json`, `version: 1`, `updatedAt: 2026-09-17T20:00:00Z`.
- Toplam **15 CloudStream repo.json kaydı**. Bu dosya doğrudan oynatma URL'si, akışın ses izi ya da tüm alt eklentilerin çalışan listesini **içermiyor**.
- Adreslerin tümü değiştirilmeden `BundledMediaSources.kt` içinde `cloudStreamRepositories` listesine alınmıştır.
- Kontrol sırasında 12 `repo.json` cevaplarında okunabilir CloudStream `pluginLists` alanları görüldü. `neoser1984/cloudstream-extensions` ve `SafakStream/SafakStream` URL'leri 404 verdi. `BronzeCloud` URL'sinin okunabilirliği teyit edilemedi.
- Bu kontroller sonradan değişebilir.

## Önemli teknik uyumluluk

| Format | Örnek | Nuvio Mobile v0.3 davranışı |
| --- | --- | --- |
| Stremio uyumlu addon | `.../manifest.json` | Nuvio'nun standart `AddonRepository.addAddon` doğrulamasıyla kurulabilir |
| Nuvio JS plugin deposu | `.../manifest.json` + `scrapers` | Standart `PluginRepository.addRepository` doğrulamasıyla kurulabilir |
| CloudStream deposu | `.../repo.json` ve `plugins.json` | Salt okunur katalog; **Nuvio JS plugin deposu olarak çalıştırılmaz** |
| CloudStream paketi | `.cs3` / Android DEX | V0.3'te yerel çalıştırma/otomatik indirme **yok** |

CloudStream eklentilerini etkinleştirmek için ayrı ve güvenlik değerlendirmesi yapılmış bir uyumluluk köprüsü/adapter gerekir. Bu paket, başka uygulamanın ikili uzantılarını sessizce çalıştırmaz.

## Gömülü Nuvio kaynakları

- **12** Stremio-uyumlu addon manifest adresi (katalog, altyazı, canlı yayın ve akış türleri).
- **11** Nuvio plugin deposu manifest adresi.
- Uygulama içi konum: **Ayarlar → Eklentiler → Yerleşik kaynak paketi**.
- İki ayrı **toplu ekle** düğmesi vardır. Kurulum kullanıcı talebi üzerine yapılır. Var olan kaynaklar değiştirilmez veya devre dışı bırakılmaz.
- Aynı URL tekrar eklenmez. Erişilemeyen veya uyumsuz manifestler başarısız sayılır. Otomatik başarılı oynatma iddiası yoktur.
- Adresler uygulamada gömülüdür; kullanıcı ayrıca dosya/kopyalama işlemi yapmadan ekleyebilir.

## Sağlayıcı metadata incelemesi (stream/audio doğrulaması değildir)

Ulaşılabilen CloudStream `plugins.json` açıklamalarında gözlenen örnek ifadeler:

| Depo | Sağlayıcı | Metadata'daki Türkçe dublaj bildirimi |
| --- | --- | --- |
| Manitux CS | DiziYou | “Türkçe dublaj” |
| Manitux CS | SetFilm | “Türkçe dublaj veya altyazı” |
| Manitux CS | Dizipal | “Türkçe altyazılı ve dublajlı” |
| Kekik | Dizilla | “Türkçe Dublaj ve altyazılı” |
| Kekik | BelgeselX | “türkçe altyazılı yada dublaj” |
| Latte | DiziFun | “türkçe dublaj , türkçe altyazılı” |
| AllForU | Dizipal | “Türkçe altyazılı ve dublajlı” |

**Bu bilgiler yalnızca sağlayıcının açıklamasından gelir.** Tek bir film/bölümün sesinde Türkçe olduğunun kanıtı değildir. Bu nedenle sınıflandırıcı bir akışta `Türkçe dublaj`, `TR DUB`, `Türkçe Seslendirme`, `Dublaj / Altyazılı` gibi **başlık/açıklama etiketlerine** ve varsa akışın bildirdiği dil metaverisine bakar. Sadece kaynak/sağlayıcı adından doğrulanmış ses sonucu üretmez.

## v0.3 doğrulama ve sınırlar

- Kotlin sınıflandırıcısı yerel testle **30 kontrolü** geçti.
- İçeriğin gerçek `Turkish (AAC)` ses kanalını kontrol eden Media3 doğrulaması **henüz eklenmedi**.
- PR için yeni Android debug APK **henüz derlenmedi**; gerçek cihaz kurulumu ve kaynak import testi gereklidir.
- Aktif altyazı filtrelerinin kullanılması, bu uygulamanın devraldığı tüm kaynakların yayın hakkı veya güvenliği bulunduğu anlamına gelmez.

## Derleme

GitHub Actions `Build Nuvio TR Dublaj (debug APK)` iş akışı önce `scripts/install_filter.py`, sonra `scripts/install_bundle.py` ile Nuvio'nun `cmp-rewrite` kaynak ağacını yamalar. Başarılı olursa `Nuvio-TR-Dublaj-debug-APK` adlı artifact oluşturur. 
