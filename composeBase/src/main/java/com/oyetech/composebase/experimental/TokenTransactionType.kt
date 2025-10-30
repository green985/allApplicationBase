package com.oyetech.composebase.experimental/*
[GRUP REHBERİ — Hangi özelliği açmak için hangi yorumları kaldırmalısın?]

[A] Kaynak & raporlama zenginliği (ör. source, note, trace) 
    -> TokenTransaction.source, TokenTransaction.note, TokenTransaction.traceId, TokenTransaction.tenantId, TokenTransaction.externalRef

[B] Kullanıcı bazlı filtre/rapor kolaylığı
    -> TokenTransaction.affectedUid

[C] İdempotensi sahipliği (anahtar kime ait?)
    -> IdempotencyRecord.ownerUid

[D] Optimistic concurrency / sıralı ledger
    -> AccountBalance.version, TokenPosting.ledgerSeq

[E] Çoklu kaynak hesaplar (PROMO, BURN vb. bütçe ayrımı)
    -> EarnRequest.campaignId

[F] Denetim izi ve izlenebilirlik
    -> AuditLog sınıfı (tamamını aç), TokenTransaction.note, TokenTransaction.traceId, TokenTransaction.externalRef, AccountBalance.lastTxId

[G] Ters kayıt / iptal (reversal/cancel)
    -> TokenTransaction.reversedOfTxId

[H] Anti-abuse / cihaz bazlı limit
    -> EarnRequest.deviceId, EarnCommand.deviceId, EarnRequest.ipv4, EarnRequest.ipv6

[I] Transfer akışı (kullanıcıdan kullanıcıya)
    -> TransferCommand sınıfı (tamamını aç)

[J] Admin harcama akışı
    -> SpendRequest sınıfı (tamamını aç)
*/

/* ==== ENUM’LAR ==== */
enum class TokenTransactionType { EARN, SPEND, TRANSFER, ADJUST, REVERSAL } // İşlem türleri (kazan, harca, aktar, düzelt, ters kayıt)
enum class TokenTransactionStatus { PENDING, COMMITTED, CANCELED }          // İşlem yaşam döngüsü
enum class PostingDirection { DEBIT, CREDIT }                                // Muhasebe satırı yönü
enum class EarnRequestStatus { PENDING, ACCEPTED, REJECTED }                 // Earn isteği durumu
enum class IdempotencyStatus { RESERVED, COMMITTED }                         // İdempotensi kayıt durumu

/* ==== ÇEKİRDEK MODELLER (MVP için aktif) ==== */

/* İstemcinin kazanç talebi; Functions doğrular ve işler. */
data class EarnRequest(
    val id: String,                 // İsteğin benzersiz kimliği (istemci veya sunucu üretebilir)
    val uid: String,                // Talebi yapan kullanıcının kimliği
    val amount: Long,               // Talep edilen token miktarı (pozitif, tam sayı)
    val source: String,             // Kazanım sebebi (örn. "daily_login", "promo_code")
    val createdAt: Long,            // İsteğin oluşturulma zamanı (epoch millis)
    val status: EarnRequestStatus,   // İsteğin işlenme durumu (PENDING→ACCEPTED/REJECTED)
    // [H] val deviceId: String?,    // Cihaz bazlı limit/analiz için opsiyonel kimlik
    // [H] val ipv4: String?,        // Anti-abuse sinyali (IP v4)
    // [H] val ipv6: String?,        // Anti-abuse sinyali (IP v6)
    // [E] val campaignId: String?   // Kampanya/bütçe kaynağı eşlemesi (PROMO vb.)
)

/* Ledger üst nesnesi; tüm postings bunun altına mantıksal olarak bağlanır. */
data class TokenTransaction(
    val id: String,                         // İşlem kimliği (idempotent akışta tekrar kullanılabilir)
    val type: TokenTransactionType,         // İşlem türü (EARN/SPEND/TRANSFER/ADJUST/REVERSAL)
    val status: TokenTransactionStatus,     // İşlem durumu (PENDING→COMMITTED/CANCELED)
    val totalAmount: Long,                  // İşlemin toplam tutarı (postings toplamı ile tutarlı)
    val createdByUid: String,               // İşlemi başlatan kullanıcı/sistem kimliği
    val createdAt: Long,                    // Oluşturulma zamanı (epoch millis)
    val idempotencyKey: String,              // Aynı isteğin tekrarını engelleyen anahtar
    // [A] val source: String?,             // İşlemin kaynağı/metası (örn. "daily_login")
    // [B] val affectedUid: String?,        // Bakiyesi etkilenen ana kullanıcı (liste/filtre için)
    // [F] val note: String?,               // Kısa serbest açıklama/nota alanı
    // [F] val traceId: String?,            // Uçtan uca iz sürme kimliği (distributed tracing)
    // [F] val tenantId: String?,           // Çok kiracılı yapıda kiracı ayıracı
    // [F] val externalRef: String?,        // Dış sistem referansı (örn. ödeme/işlem id)
    // [G] val reversedOfTxId: String?      // Ters kayıt/iptal edilen işlemin id’si
)

/* Çift kayıt muhasebe satırı. Her işlemde min. 2 satır (DEBIT/CREDIT) olmalı. */
data class TokenPosting(
    val id: String,                         // Muhasebe satırı kimliği (tekil)
    val transactionId: String,              // Bağlı olduğu işlem kimliği (TokenTransaction.id)
    val accountId: String,                  // Etkilenen hesap (örn. "USER:<uid>", "TREASURY")
    val direction: PostingDirection,        // Satır yönü (DEBIT/CREDIT)
    val amount: Long,                       // Tutar (pozitif, tam sayı)
    val createdAt: Long,                     // Satır oluşturulma zamanı (epoch millis)
    // [D] val ledgerSeq: Long?             // Global/hesap bazlı sıralı numara (okunabilirlik & denetim)
)

/* Materialized bakiye; yalnız Functions yazar. */
data class AccountBalance(
    val accountId: String,                  // Hesap kimliği (örn. "USER:<uid>", "TREASURY")
    val amount: Long,                       // Güncel malzeme edilmiş bakiye
    val updatedAt: Long,                     // Son güncellenme zamanı (epoch millis)
    // [D] val version: Long?,              // Optimistic concurrency için sürüm
    // [F] val lastTxId: String?            // Bu bakiyeyi son etkileyen işlem kimliği
)

/* Aynı isteğin yeniden işlenmesini engellemek için idempotensi kaydı. */
data class IdempotencyRecord(
    val key: String,                        // İdempotensi anahtarı (örn. sha256(uid+source+day))
    val transactionId: String?,             // Commit edilen işlem kimliği (varsa)
    val status: IdempotencyStatus,          // Kayıt durumu (RESERVED→COMMITTED)
    val createdAt: Long,                    // Oluşturulma zamanı (epoch millis)
    val updatedAt: Long,                     // Son güncelleme zamanı (epoch millis)
    // [C] val ownerUid: String?            // Anahtarın sahibi (çoklu kiracı/ayırma için)
)

/* Functions’a giden komut DTO’su (istemci + sunucu normalize eder). */
data class EarnCommand(
    val uid: String,                        // Kazanımı alacak kullanıcı
    val source: String,                     // Kazanım sebebi (iş kuralı için)
    val amount: Long,                       // Talep edilen miktar (kuralla normalize edilecek)
    val idempotencyKey: String,             // Tekrar çağrıda aynı sonucu vermek için anahtar
    val now: Long,                           // Sunucu zamanı (epoch millis)
    // [H] val deviceId: String?            // Cihaz bazlı limit/analiz (opsiyonel)
)

/* İstemciye dönen sonuç. */
data class EarnResult(
    val ok: Boolean,                        // Başarı durumu (true/false)
    val transactionId: String?,             // Commit edilen işlem id’si (başarılıysa)
    val newUserBalance: Long?,              // Yeni kullanıcı bakiyesi (başarılıysa)
    val rejectedReason: String?,             // Red sebebi (başarısızsa kısa kod/metin)
    // [F] val appliedRuleBucket: String?   // Uygulanan/engelleyen kural kovası (raporlama)
)

/* ==== OPSİYONEL MODELLER (özellik açıldığında yorumdan çıkar) ==== */

/* [J] Admin harcama isteği; yalnız admin oluşturur ve Functions işler. */
// data class SpendRequest(
//     val id: String,                      // Harcama isteği kimliği
//     val targetUid: String,               // Bakiyesi düşecek kullanıcı
//     val amount: Long,                    // Harcanacak tutar
//     val reason: String,                  // Gerekçe (kısa metin)
//     val createdByUid: String,            // Admin kullanıcı id
//     val createdAt: Long,                 // Oluşturulma zamanı
//     val status: String                   // PENDING/APPROVED/REJECTED (ileride enum yapılabilir)
// )

/* [I] Kullanıcıdan kullanıcıya transfer komutu (ileriki faz). */
// data class TransferCommand(
//     val fromUid: String,                 // Gönderen kullanıcı id
//     val toUid: String,                   // Alan kullanıcı id
//     val amount: Long,                    // Transfer tutarı
//     val idempotencyKey: String,          // İdempotensi anahtarı
//     val now: Long                        // İşlem zamanı (epoch millis)
// )

/* [F] Denetim izi; önemli olayları kayıt altına almak için. */
// data class AuditLog(
//     val id: String,                      // Log kimliği
//     val event: String,                   // Olay adı (örn. TX_COMMITTED, RULE_REJECT)
//     val refId: String?,                  // İlgili işlem/istek kimliği
//     val actorUid: String?,               // Olayı tetikleyen kullanıcı/sistem
//     val createdAt: Long,                 // Log zamanı
//     val details: String?                 // JSON/text detay
// )