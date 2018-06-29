package abos.server

class Settings {
    String key
    String value
    static constraints = {
        key blank: false, size: 1..45, unique: true
        value size: 1..100
    }
}
