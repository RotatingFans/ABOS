package abos.server

class Year {
    String year
    static constraints = {
        year size: 4..4, unique: true, blank: false
    }
}
