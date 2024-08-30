import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class FirebaseRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    // Email'i hash'leyerek kullanıcıya özel bir alan oluşturuyoruz
    private fun hashEmail(email: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val result = digest.digest(email.toByteArray())
        return result.fold("") { str, it -> str + "%02x".format(it) }
    }

    // Kullanıcıya özel chat referansı
    private fun getUserChatRef(email: String): DatabaseReference {
        val hashedEmail = hashEmail(email)
        return database.getReference("users/$hashedEmail/chats")
    }

    // Kullanıcıya özel image referansı
    private fun getUserImageRef(email: String): DatabaseReference {
        val hashedEmail = hashEmail(email)
        return database.getReference("users/$hashedEmail/images")
    }

    // Kullanıcıya özel chat ve image kategorileri oluşturma fonksiyonu
    fun createUserSpecificCategory(email: String) {
        val hashedEmail = hashEmail(email)
        val userCategoryRef = database.getReference("users/$hashedEmail")

        // Chat kategorisi oluştur
        userCategoryRef.child("chats").setValue(true)

        // Image kategorisi oluştur
        userCategoryRef.child("images").setValue(true)
    }

    // Chat ekleme fonksiyonu
    fun addChat(email: String, message: String, sender: String) {
        val chatRef = getUserChatRef(email).push()
        val chatData = mapOf(
            "message" to message,
            "sender" to sender,
            "timestamp" to System.currentTimeMillis()
        )
        chatRef.setValue(chatData)
    }

    // Image ekleme fonksiyonu
    fun addImage(email: String, imageUrl: String) {
        val imageRef = getUserImageRef(email).push()
        val imageData = mapOf(
            "imageUrl" to imageUrl,
            "timestamp" to System.currentTimeMillis()
        )
        imageRef.setValue(imageData)
    }

    // Kullanıcıya özel chat listesini getirme
    fun getUserChats(email: String): DatabaseReference {
        return getUserChatRef(email)
    }

    // Kullanıcıya özel image listesini getirme
    fun getUserImages(email: String): DatabaseReference {
        return getUserImageRef(email)
    }
}