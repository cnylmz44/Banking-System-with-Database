# HOMEWORK
Arkadaşlar merhaba bu ödevimizde önceki ödevimize ufak bir ekleme yapacağız ve data saklama katmanımızı değiştireceğiz.

1 - Öncelikle ödevinizde daha önce opsiyonel dediğim bazı şeyleri zorunlu hale getiriyoruz. Ekstradan account'a iki yeni bilgi tutacağız.
* Son güncellenme tarihi
* Silinmiş bir account mu değil mi buna dair boolean(tinyint) bir kolon
* Kur çevrimi kesinlikle collectapi'den alınacaktır.

Ödevimizde accountta herhangi bir işlem yapıldıgı anda son guncelleme tarihini o an olarak güncellemenizi istiyorum.

2 - Yeni bir REST servisi ekliyoruz account silme işlemi için.

```
    @RestController
    public class AccountController {
    	
    	@Autowired
    	private AccountRepository repository;
    	
    	@DeleteMapping("/accounts/{id}")
    	public ResponseEntity<> delete(@PathVariable int id) {
    		//silme işlemi
    	}
    }
```
Silme işleminde kaydın databaseden silinmesini istemiyorum, database'de account silinmişliğine dair eklediğimiz kolonu 1'e(true) çekmenizi istiyorum. (Sektörde bu silme yaklaşımına **soft delete** denmektedir. Siz mesela Facebookta birşeyi silince siliniyor sanıyorsunuz ya hah işte o silinmiyor, sadece böyle bir kolondaki  değer true'ya çekiliyor!)

3 - Ödevimizde accountları logları vs.. file'da tutmuştuk. Bu katmanı tamamen database'e taşıyacağız. Burada
* normal jdbc (5 Puan)
* spring-data-jdbc(7 Puan)
* myBatis(10 Puan)

üçünden birisini kullanarak data katmanınızı tamamen SQL'e taşımanızı istiyorum(Kullandıgınız kütüphane yanında yazdığı şekilde puanınızı etkileyecektir!). Burada database'imiz MYSQL olacaktır. [Mysql Installer](https://dev.mysql.com/downloads/installer/) bu linkten mysql installer aracılığıyla Mysql sunucusunu yükleyebilirsiniz.

Eğer data katmanını düzgünce interface ile yaptıysanız burası çocuk oyuncağı arkadaşlar. Interfaceleri tanımlayan yeni bir sınıf yaz yeni sınıfta artık ORM ile methodları doldur. Neden bazı katmanları interface'le belirlemeliyiz sorusunu da; eğer interface ile yapmadı iseniz acı bir cevap alacaksınız maalesef!!

Data katmanından kasıt accountları persist etmek ve logları kafkadan aldıktan sonra persist etmek. Yani iki tane table'a ihtiyacınız vardır. Her tablo auto increment id isminde primary key kolonuna sahip olmalıdır.

Transfer gibi daha önce file'dan update edilen data artık SQL ile database'den yapılacaktır. Ve transfer işleminin kesinlikle transaction içinde yapılmasını istiyorum.

accounts{
    columns...
    is_deleted tinyint(1) default 0
    last_updated DATETIME
}

logs{
    columns...
}
Ve daha önce yazdığınız servislerde kullandıgınız accountNumber parametrelerinin id olarak değiştirilmesini bekliyorum.


```
    @RestController
    public class AccountController {
    	
    	@Autowired
    	private AccountRepository repository;
    	
    	//OLD
    	@GetMapping("/accounts/{accountNumber}")
    	public ResponseEntity<Account> detail(@PathVariable int accountNumber) {
    		//silme işlemi
    	}
    	
    	//NEW
    	@GetMapping("/accounts/{id}")
    	public ResponseEntity<Account> detail(@PathVariable int id) {
    		
    	}
    }
```
4 - Detay cevabında istek yapanlara LAST_MODIFIED headeri ile database'den aldığımız son güncelleme tarihini göndereceğiz.

```
    @RestController
    public class AccountController {
    	
    	@Autowired
    	private AccountRepository repository;
    	//NEW
    	@GetMapping("/accounts/{id}")
    	public ResponseEntity<Account> detail(@PathVariable int id) {
    		....
    		....
    		return ResponseEntity
    		.ok()
    		.body(account)
    		.lastModified([last_modified_date from database column])
    	}
    }
```


