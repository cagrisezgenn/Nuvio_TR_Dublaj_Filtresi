# Nuvio Mobile — Türkçe Dublaj Filtresi, v0.1 (kaynak kodu paketi)

## Amaç
Tüm Eklentiler (Stremio addons) ve tüm Pluginler (JavaScript sağlayıcılar)
AÇIK kalır. Yalnızca film / dizi kaynak listesi ekranda filtrelenir.
Depoların yapılandırması, kaynak arama ve oynatma algoritması değişmez.

## Sağlanan özellik
- `Tümü`: bütün sonuçlar.
- `TR Dublaj (etiket)`: dosya adı / açıklama / bildirilmiş dil etiketi Türkçe ses diyenler.
- `TR Çift Ses`: Türkçe içeren çift ses bilgisi bildirenler.
- `Belirsiz`: Türkçe ses bildirimi olmayanlar; Türkçe altyazıdan hareketle dublaj varsayılmaz.
- Sonuç sayısı ve **etiket doğrulaması ≠ gerçek ses doğrulaması** uyarısı.
- Hem kaynak listesinin grup seçimini hem bütün sağlayıcıların etkinliğini korur.
- Her yeni içerikte varsayılan `Tümü`, filtre ekran içinde değiştirilebilir.

## Sınır ve güvenilirlik
Bu v0.1 **gerçek ses parçasını analiz etmez**. HLS/DASH/MP4/MKV dosyalarının
medya track metaverisini okuyacak ve erişilebilir akışları doğrulayacak
Media3 tabanlı asenkron ikinci aşama henüz uygulanmadı. Bazı sağlayıcılar
Türkçe ses başlıklarını yanlış işaretleyebilir; belirsiz kaynaklar
bilinçli olarak ayrı gruptadır. Bu nedenle hiçbir sonuca
"kesin Türkçe dublaj" rozeti verilmez.

## Windows'ta kurulum

1. `git` ve Android Studio (Android SDK + JDK) kurun.
2. Kaynağı indirin:

   `git clone --branch cmp-rewrite https://github.com/NuvioMedia/NuvioMobile.git`

3. Bu paketi açın, `install_windows.bat "C:\...\NuvioMobile"` çalıştırın.
   Alternatif: `py -3 scripts\install_filter.py "C:\...\NuvioMobile" --check`
4. `build_windows.bat "C:\...\NuvioMobile"` çalıştırın.
5. Oluşan `fullDebug` APK yolunu `androidApp/build/outputs/apk` altında bulun.
   Dağıtım ve hata ayıklama varyantının dosya adı upstream projeye göre değişebilir.
6. Resmî projede debug sürümünün uygulama kimliği `com.nuviodebug.com`,
   normal uygulamanın kimliği `com.nuvio.app` olarak belirlenmiş.
   Bu nedenle fullDebug APK normal Nuvio yanında ayrı kurulabilir.
   Ancak geçmiş bir debug Nuvio kurulumunuz varsa imza çakışabilir.

## Geri alma
`StreamsScreen.kt.trdub-original.bak` dosyasını `StreamsScreen.kt`
yerine koyun ve iki yeni Kotlin dosyasını silin.

## Kaynak kod dayanağı
20.10.2026 tarihinde kontrol edilen resmî depo:
https://github.com/NuvioMedia/NuvioMobile/tree/cmp-rewrite

İncelenen dosyalar:
- `composeApp/src/commonMain/kotlin/com/nuvio/app/features/streams/StreamsScreen.kt`
- `composeApp/src/commonMain/kotlin/com/nuvio/app/features/streams/StreamModels.kt`
- `composeApp/src/commonMain/kotlin/com/nuvio/app/features/streams/StreamsRepository.kt`

Kaynak kod lisansı: NuvioMobile GPL-3.0.
Geliştirmenin aynı lisansın yükümlülüklerine uygun dağıtılması gerekir.

## Test
Linux/macOS üzerinde örnek saf Kotlin testini çalıştırın:

`kotlinc tests/StreamModelsTestStub.kt src/TurkishDubFilter.kt tests/FilterTest.kt -include-runtime -d /tmp/trdubtest.jar`

`java -jar /tmp/trdubtest.jar`

Not: Burada Kotlin mantık testi yapılabilir. Gerçek APK derlemesi Android SDK,
Gradle bağımlılıkları ve resmî depo kaynaklarının yerel kopyasını gerektirir.

## Android Studio olmadan GitHub Actions ile APK oluşturma

1. Bu ZIP içeriğini (gizli `.github` klasörü dahil) kendi GitHub deponuza yükleyin.
   Kodu herkese açık yayımlıyorsanız GPL-3.0 koşullarını koruyun.
2. GitHub deponuzda `Actions` sekmesini açın.
3. `Build Nuvio TR Dublaj (debug APK)` iş akışını seçip `Run workflow` tıklayın.
4. Derleme başarılı olursa bu çalışmanın `Artifacts` bölümünde
   `Nuvio-TR-Dublaj-debug-APK` dosyası bulunur. ZIP'i açıp APK'yı
   telefonunuza kurabilirsiniz.

Bu iş akışı resmî NuvioMobile `cmp-rewrite` dalını GitHub'da yeniden klonlar,
yalnızca iki Kotlin özellik dosyası ekler ve kaynak seçimi ekranına
filtre arayüzünü uygular. Güncel upstream yapısı beklenenle uyuşmazsa
betik bilinçli olarak durur; yanlış kaynak kodunu sessizce değiştirmez.
GitHub Actions APK'sı burada oluşturulmadı, çünkü bu ortamda resmi
depoyu klonlama/Android SDK ile derleme olanağı yok.
