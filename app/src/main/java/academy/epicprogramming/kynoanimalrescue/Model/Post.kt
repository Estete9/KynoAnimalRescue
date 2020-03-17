package academy.epicprogramming.kynoanimalrescue.Model

class Post {

    private var postid: String = ""
    private var postimage: String = ""
    private var publisher: String = ""
    private var description: String = ""

    constructor()
    constructor(postid: String, postimage: String, publisher: String, description: String) {
        this.postid = postid
        this.postimage = postimage
        this.publisher = publisher
        this.description = description
    }

    fun getPostId(): String {
        return postid
    }

    fun getPostImage(): String {
        return postimage
    }

    fun getPublisher(): String {
        return publisher
    }

    fun getDescription(): String {
        return description
    }

    fun SetPostId(postid: String){
        this.postid = postid
    }
    fun SetPostImaged(postimage: String){
        this.postimage = postimage
    }
    fun SetPublisher(publisher: String){
        this.publisher = publisher
    }
    fun SetDescription(description: String){
        this.description = description
    }

}