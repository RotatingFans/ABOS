package abos.server

class UserManager {
    User manage
    User user
    static constraints = {
/*        manager nullable: false
        user nullable: false*/
    }

    static mappedBy = [manage: "none", user: "none"]
}
