# Nuvio TR Dublaj v0.4 — Kaynak inceleme ve kullanıcı ses doğrulaması

## Tespit edilen iki ayrı veri kaynağı
- `CSRepoManager_User.apk` fiziksel olarak `assets/central_repos.json` içinde **15 eski CloudStream** deposu tutuyor (`updatedAt: 2026-09-17T20:00:00Z`).
- Aynı APK'nın derlenmiş kodunda GitHub'dan uzaktan güncelleme yapan `https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/central_repos.json` adresi bulundu. **2026-10-22 14:07:43 UTC** tarihli güncel JSON'da 60 kayıt var: **31 CloudStream** ve **29 Nuvio**.
- Kullanıcının istediği 29 Nuvio girdisi, **yalnızca bu ikinci çevrimiçi listeden** donduruldu. 31 CloudStream kaydı ve herhangi bir Play runtime paketi v0.4'te yok. "PLAY KATALOG" isimli bir kayıt, listede **Nuvio eklentisi** olarak işaretli olduğundan 29 kaydın içindedir; Play plugin motoru anlamına gelmez.

## v0.4 APK'da gömülü olan tam 29 Nuvio kaydı

| Sıra | Liste adı | Kurulum türü | Kaynak URL |
|---:|---|---|---|
| 1 | wio sinema plugin | Plugin | `https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/main/manifest.json` |
| 2 | PLAY KATALOG | Eklenti | `https://play-nuvio-zel-katalog-st-dyosu.ai.studio/` |
| 3 | JUSTEMİR84 plugin | Plugin | `https://raw.githubusercontent.com/justemir84/nuvio-dexter-plugin/main/manifest.json` |
| 4 | izlelan katalog | Eklenti | `https://katalog.ayruki.workers.dev` |
| 5 | jasoelys katalog | Eklenti | `https://katalog.jasoelys.workers.dev/` |
| 6 | jasoleys | Plugin | `https://nuvio.jasoelys.workers.dev/manifest.json` |
| 7 | HANS.MEGA | Plugin | `https://raw.githubusercontent.com/pnthancyb/hans-mega/main/manifest.json` |
| 8 | GSREPO | Plugin | `https://raw.githubusercontent.com/csprofesor/csnuvio/main/manifest.json` |
| 9 | ANTHOLOGY PLUGİN | Plugin | `https://raw.githubusercontent.com/falsisdev/anthology/main/manifest.json` |
| 10 | izlelan | Plugin | `https://izlelan.com/manifest.json` |
| 11 | DİZİBOX | Plugin | `https://raw.githubusercontent.com/kratoskrator/dizibox-nuvio/main/manifest.json` |
| 12 | hdfilmcehennemi | Plugin | `https://raw.githubusercontent.com/kratoskrator/hdfilmcehennemi-nuvio/main/manifest.json` |
| 13 | BRONZE CLOUD | Eklenti | `https://raw.githubusercontent.com/dr-octagon/Nuvio/main/manifest.json` |
| 14 | PATRON | Plugin | `https://raw.githubusercontent.com/patr0nq/nuvioaddons/main/manifest.json` |
| 15 | SREAİKER | Eklenti | `https://streailer.elfhosted.com/%7B%22language%22%3A%22tr-TR%22%2C%22externalLink%22%3Afalse%2C%22showRecap%22%3Afalse%2C%22onlyRecaps%22%3Afalse%7D/manifest.json` |
| 16 | mooncrown | Plugin | `https://raw.githubusercontent.com/liberta09/CloudStreamRepoManager/main/nuvio/manifest.json` |
| 17 | wekmed | Plugin | `https://raw.githubusercontent.com/Wekmed/nuvio/refs/heads/main/manifest.json` |
| 18 | nexus | Eklenti | `https://nexuszen.vercel.app/manifest.json` |
| 19 | SKCZ TORRENT | Eklenti | `https://61ab9c85a149-skcz-torrents.baby-beamup.club/manifest.json` |
| 20 | MOTORSPORT HUB | Eklenti | `https://stream.prototipo.nl/%7B%7D/manifest.json` |
| 21 | sİNEWİX | Eklenti | `https://sine-wix.vercel.app/manifest.json` |
| 22 | TORRENTİNO | Eklenti | `https://torrentio.strem.fun/manifest.json` |
| 23 | İZLEMELİ | Eklenti | `https://izlemeli.github.io/nuvio/tr/manifest.json` |
| 24 | wiospor | Eklenti | `https://raw.githubusercontent.com/Wiojelt/Nuvio-Addons/live/wiospor/manifest.json` |
| 25 | MANİTUX | Plugin* | `https://raw.githubusercontent.com/manitux-app/nuvio-plugins/refs/heads/main/manifest.json` |
| 26 | EUROSPORT | Eklenti | `https://tvvoo.hayd.uk/cfg-tr-hc1/manifest.json` |
| 27 | NUVİO KATALOG | Eklenti | `https://catalog.nuvio.tv/%7B%22language%22%3A%22tr-TR%22%2C%22region%22%3A%22TR%22%7D/manifest.json` |
| 28 | ANTHOLOGY NUVİO | Eklenti | `https://falsisdev.github.io/anthology/stremio/manifest.json` |
| 29 | ALTYAZI | Eklenti | `https://altyazi-stremio.online/manifest.json` |

\* MANİTUX üst listede "addon" yazıyor fakat `manifest.json` gerçek anlamda `scrapers` alanı içerdiğinden **plugin** olarak sınıflandırıldı. Böylece **16 Nuvio addon + 13 Nuvio JS plugin** var. Kaynaklar **katalog/preset olarak gömülüdür**: kullanıcı iki ayrı **Topluca ekle** düğmesini kullanmadan uzak kod kendiliğinden yüklenmez. Hatalı veya erişilemeyen URL'ler doğrulama sırasında raporlanır.

## Gerçek Türkçe dublaj hakkında

- `contentLanguage:["tr"]`, `language:"tr"`, `"tr-TR"` veya kaynak ismindeki "Türkçe" **yalnızca sağlayıcı/katalog dilini** anlatabilir.
- `Dublaj / Altyazılı` ibaresi de hem altyazı hem dublaj seçeneklerinin sunulabileceğini söyler, tekil akışın Türkçe sesinin doğrulaması değildir.
- Oynatıcıdaki **Ses parçaları → Turkish (AAC)** / **Turkish (EAC3)** gibi bir ses kanalının o kaynağa bağlı olarak seçilebilmesi daha güçlü bir doğrulamadır.
- Manifestlerde taranabilen örnek depolardaki toplam 402 sağlayıcı kaydının 221'inde `contentLanguage: tr` görüldü. **Birçok kaynak Mooncrown ortak paketinde tekrarlandığı için bu 221 benzersiz sağlayıcı sayısı değildir.**
- Bazı manifestlerin URL'lerine web üzerinden erişilemiyor; bu kayıtların çalıştığı iddia edilmez.

## İlk kullanıcı doğrulama partisi (10 sağlayıcı)

| No | Nuvio plugin deposu | Depoda aranacak tam sağlayıcı adı | Metadata durumu | Kullanıcı sonucu |
|---:|---|---|---|---|
| 1 | wio sinema plugin | WioSinema • FilmModu | `tr,en` | ? |
| 2 | wio sinema plugin | WioSinema • FilmMakinesi | `tr,en` | ? |
| 3 | wio sinema plugin | WioSinema • DiziMom | `tr,en` | ? |
| 4 | ANTHOLOGY PLUGİN | SineWix | `tr` | ? |
| 5 | ANTHOLOGY PLUGİN | FilmModu | `tr` | ? |
| 6 | ANTHOLOGY PLUGİN | DiziYou | `tr` | ? |
| 7 | ANTHOLOGY PLUGİN | HDFilmCehennemi | `tr,en` | ? |
| 8 | PATRON | PatronFilmMakinesi | `tr` | ? |
| 9 | GSREPO | Dizilla | `tr` | ? |
| 10 | MANİTUX | !DiziYou | `tr` | ? |

### Test adımları (her sağlayıcı için)
1. Nuvio TR APK → **Ayarlar → İçerik ve Keşif → Pluginler/Uzantılar → Depolar**. Gerekirse kaynağı yükleyin ve **sağlayıcının etkin** olduğundan emin olun; hiçbir başka sağlayıcıyı kapatmayın.
2. **Ayarlar → Oynatma → Stream Selection / Akış Seçimi → Manual / Manuel** yapın (otomatik ilk kaynağı oynatmasın). **Tercih edilen ses dili: Turkish/Türkçe** seçilebilir ama bu, kaynak doğrulaması değildir.
3. Arama bölümünden önceden Türkçe ses bulduğunuz bir film/diziye gidin. Dizi için sezon/bölümü sabitleyin.
4. Kaynak ekranında **Tümü** filtresini seçin (TR filtrelerini şimdilik kullanmayın). Tab/başlıkta ilgili depoyu ve sağlayıcıyı bulun.
5. Kaynağa dokunup oynatıcıyı açın. **Ses parçaları / Audio Tracks** menüsünü açın.
6. **Turkish (AAC/EAC3/...)** seçilebiliyor ve konuşmalar Türkçe duyuluyorsa **EVET**. Yalnız altyazı varsa **HAYIR**. Kaynak gelmiyorsa/oynamıyorsa **ÇALIŞMIYOR**. Menü dil göstermiyorsa **BELİRSİZ**.
7. Sonucu `No | Evet/Hayır/Çalışmıyor/Belirsiz | Film/Dizi adı | Ses parçaları` şeklinde bildirin. Her sağlayıcı ve her film için ses değişebileceğinden, başarı sonucu tüm sağlayıcının bütün akışlarına otomatik genellenmeyecek.

## Paket korumaları
- Yalnızca 29 Nuvio preset URL'si vardır.
- Gömülü CloudStream `repo.json` veya Play runtime/plugin paketleri yoktur.
- Tüm kaynakları açık tutma hedefi korunur; import mevcut depoları kaldırmaz, cihazda kapalı olan sağlayıcıları kendiliğinden değiştirip açmaz.
- Bu revizyonda gerçek medya ses kanalı analizine yönelik yeni Media3 probe eklenmemiştir.
