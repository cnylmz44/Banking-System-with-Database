# TRANSACTIONS

Arkadaşlar database yönetim sistemleri verinin doğruluğu, güvenliği, hızlı erişimi gibi konuları sağlamak durumundadır. Yoksa neden kullanalım ki?
Bunları yapmak için koca bir külliyat vardır. Birçok konsept ve yaklaşım mmevcuttur. **Locking, Transaction, MVCC, 2PL(Two Phase Locking), Pessimistic Locking, Optimistic Locking, Isolation vb...**

Bir database sistemi temelde **ACID** ismini veriğimiz 4 temel özelliği sağlamalıdır.

* Atomicity
    * Atomicity belli bir işlem kümesinin ya hep ya hiç mantığında gerçekleşmesidir. Yani ya hepsi başarılı olmalı yada herhangi bir problem durumunda sanki hiçbiri çalışmamış gibi ilk haline dönmelidir datamız
* Consistency
* Isolation
    * Paralel bir şekilde aynı data üstünde işlem yapan iki farklı client'in birbirlerinin yaptığı işlemlerden ne kadar izole olmasına dair birşeydir.
* Durability

İşte bunları yaparken kullandığımız en önemli yapılardan birisi Transactionlardır. 
Transaction bizim başlatabildiğimiz sonuçta hata olmadığında sonuçların kalıcı hale gelmesini sağlayabildiğimiz veya hata olması durumunda yapılan işlemlerin geri alınmasını sağlayabildiğimiz temel yapımızdır.

Transaction akışı
* Transaction başlat (Begin transaction)
* Querilerini çalıştır (bir veya daha fazla)
* Sıkıntı yok ise sonuçların kalıcı hale gelmesini sağla (Commit)
* Sıkıntı var ise sıkıntı olana kadar ne kadar çalışmış query varsa geri alınmasını sağla(rollback)

Arkadaşlar dikkat her adımda gerekli komutları database'e biz gönderiyoruz. **Yani bir transaction için basitçe minimum 3 kere database'e gidilir**.(Begin, query, commit or rollback)

Normalde **JDBC**'deki driverlarda veya database sunucularında her query bir transaction içinde çalışır, query çalıştıktan sonra otomatik olarak commit isteğide gönderilir. (**autocommit mode** denir buna, default olarak her query bu şekilde çalışır)

Ancak biz istediğimiz zaman **birden fazla query'nin çalışması üzerine transaction başlatabiliriz(begin), bunları daha commit veya rollback yapabiliriz**. 

Şimdi şöyle bir senaryo düşünelim. Ben birisine para transferi yapıyorum. Burda basitçe minimum iki query çalışmalıdır. 

```
    UPDATE users SET balance = balance - miktar WHERE id = gonderici_id
    UPDATE users SET balance= balance + miktar WHERE id = alıcı_id
```

İşte tam burada ilk query çalıştıktan sonra ikinci query çalışamadan bir hata olduğunu düşünün. Elektrikler gitti, kodda bir bug oldu, database yoğunluktan cevap veremedi vb... Ne olacak?

Benden para düştü, diğer hesaba gidemedi. Ortada kayıp bir para var kimseye gitmeyen. İşte bunun gibi işlerde **bir query grubunun atomic bir şekilde ya hep ya hiç mantıgına göre çalışmasını sağlamak istiyoruz**. İşte burada **transaction** devreye giriyor.

Bir transaction başlatıp işlemlerin atomic bir şekilde yapılmasını veya hiç yapılmamış gibi olmasını sağlayabiliriz.

## TRANSACTION YARATMA

Arkadşalar JDBC'de connection nesnesi üstünden 

```
    conn.setAutoCommit(false);
```

şeklinde bir çağrı yaparak auto commit ayarını kapatırız. Yani her bir query için commit işlemi yapılmasının önüne geçeriz. Bu aşamadan sonra **commit veya rollback gelene kadar yazdığımız tüm queryler bir transaction içinde** olacaktır. Yani transaction başlamış oldu.

Daha sonra querylerimizi yazarız.
```

    String queryOne = "UPDATE courses SET name = \"One\" WHERE id = ?";
	PreparedStatement ps = conn.prepareStatement(queryOne);
	ps.setInt(1, 1);
	ps.executeUpdate();
	
	String queryTwo = "UPDATE courses SET name = \"Two\" WHERE id = ?";
	ps = conn.prepareStatement(queryTwo);
	ps.setInt(1, 2);
	ps.executeUpdate();
```		

Querylerimiz çalışır ama ufak bir farkla, buradaki değişimler bir yere kadar kalıcı olarak database'de saklanmaz. Yani **bu update'in sebep olduğu değişimler geçici bir yerde tutuluyor; diske kalıcı olarak yazılmadı diyebiliriz kabaca**.

Eğer kodumuzda bir sıkıntı çıkmadı ise, herhangi bir exception durumu yok ise 

```
    conn.commit();
```

diyerek database sunucusuna "tamam işler başarılı bir şekilde bitti, artık değişiklikleri kalıcı olarak diske al" deriz. **İşte bu adımda yapılan herşey gerçekten database'e yansımış olur.**

Eğer kodumuzda bir sıkıntı çıktı ve ikinci query işlenemedi ise burada önceki query'ininde geçerliliği kalmadığını yapılan update'in diske yansıtılmaması gerekitğini yani geri alınması gerektiğini database sunucusuna söylememiz lazım. İşte bunu da

```
    conn.rollback();
```

diyerek sunucuya söyleyebilmekteyiz. **İşte bu aşamada database bizim işlemlerimiz başlamadan önceki haline dönmüştür**. Şöyle koda tam haliyle bakacak olursak;

```
    Connection conn = null;
	try {

		Class.forName("com.mysql.cj.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/test?useServerPrepStmts=true";
		conn = DriverManager.getConnection(url, "nusd", "nusddev1234");
		conn.setAutoCommit(false);
		
		String queryOne = "UPDATE courses SET name = \"One\" WHERE id = ?";
		PreparedStatement ps = conn.prepareStatement(queryOne);
		ps.setInt(1, 1);
		ps.executeUpdate();

		String queryTwo = "UPDATE courses SET name = \"Two\" WHERE id = ?";
		ps = conn.prepareStatement(queryTwo);
		ps.setInt(1, 2);
		ps.executeUpdate();
		conn.commit();
		
	    String queryThird = "UPDATE courses SET name = \"Third\" WHERE id = ?";
		ps = conn.prepareStatement(queryThird);
		ps.setInt(1, 3);
		ps.executeUpdate();
		conn.commit();

	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		conn.rollback();
		e.printStackTrace();
	}  
```

Burada **commit()** çağrısından sonra yeni bir query çalıştırsanız artık başka bir transactiondasınız demektir. Veya **setAutoCommit(false)** demeden önce bir query çalıştırdıysanız buda başka bir transactionun parçasıdır.

Yani özetle
**setAutoCommit(false)** çalıştırılır transaction başlamış olur.
**commit()** veya **rollback()** o anki transactionu sonlandırır ve yeni bir transactiona geçilir.

##ISOLATION

Arkadaşlar isolation paralelde çalışan iki veya daha fazla transactionun birbiririn yaptığı(ama commit etmediği) değişimlerden haberdar olup olmamasına dair bir yöntemdir. 
Mesela bir transaction çalışmaya başladı bir recordun bir kolonunu update etti. İşi bitmedi devam ediyor. Yani commit yapmadı.

Diğer bir transaction bu update olan recordun update olan kolonunu okumak istiyor. İşte burada acaba diğer transactionun update'ini görmeli mi yoksa commit edilmediği için görmemeli midir?

Burda ikiside olabilir. Hangisinin olacağını belirleyen şeye **isolation level** diyoruz. Her bir transaction başkalarının yaptığı işlemlerin kendi işlerine ne kadar yanısyacağını ayarlayabilir.

```
    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
```

Burada 4 farkl level vardır
* READ_UNCOMMITED
    * Bu levelde başkalarının yapıp commit etmediği değişimleri görebilmekteyiz. Ama işte sıkıntı şu ki bu datayı görüp bunla işlem yaptık diyelim, daha sonra diğer transaction rollback yaptı! Yani biz aslında hiç varolmayan bir datayı doğru kabul edip üstünden işlem yaptık. Buna dirty read denmektedir. BU levelde dirty read olabiliyor.
* READ_COMMITTED
   * Bu levelde başkalarının paralelde yaptığı değişimi göremiyoruz denebilir kabaca. Yani başka bir transaction benim okdugum yeri update etmiş olsa bile o commit yapana kadar ben kolonun eski değerini görürürüm. Yani dirty read mümkün değildir.
* TRANSACTION_REPEATABLE_READ
* TRANSACTION_SERIALIZABLE

Son ikiye girmiyorum, kafanızı karıştırmak istemiyorum, merak eden için şu linki incelemek faydalı olacaktır. [Isolation Levels](https://www.geeksforgeeks.org/transaction-isolation-levels-dbms/)  

### HATA NEREDE OLABILIR

Arkadaşlar transaction mekanizması daha iyi anlayabilmek için bazı hata durumlarına bakalım
* Mesela bir query çalıştırdık commit yapamadan bilgisayarımız bozuldu. Bu durumda uzak bir bilgisayardaki database sunucusu bir süre bizden commit veya rollback bekler, bunu alamayınca kendisi otomatik rollback yapar.
* Sunucuya query'i gonderdik çalıştı, sonra bir query daha gonderecektik ki database sunucusu down oldu(elektriği gitti veya bulundugu bilgisayar arıza verdi diyelim), bu durumda biz bağlantı hatasını aldık rollback yapmaya çalıştık ama sunucu gitti rollbackte yapamayız ki! Burada sunucu kendine gelip yeniden başladığında loglarına bakıp yarım kalmış transactionları tollback yapacaktır.
* Bir query gönderdik, sonra bir query daha bu query'imizde hata varmış sunucudan bize hata döndü. Sunucu burda hemen rollback yapmaz o geliştiricinin sorumluluğundadır. İşte bu aşamada biz rollback çağrısını yapmalıyız açıkca veya bazen hatayı handle eden bir kod vardır o duzgun query gönderir rollbackede gerek kalmaz. Yani Uygulama kodu tarafında şeylerden biz sorumluyuz. Geri kalan konularda database sunucusu commit gelmediği gelemediği sürece rollback yapar.








