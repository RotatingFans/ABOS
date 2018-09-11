package abos.server

class UserYear {
    static belongsTo = [user: User, year: Year]
    String status
    Groups group
    static constraints = {
        status size: 1..255
    }
    static restsearch = [
            user     : [field: 'user.id'],
            id       : true,
            'year'   : [field: 'year.id'],
            'year.id': true
    ]
}