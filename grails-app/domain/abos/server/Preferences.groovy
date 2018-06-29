package abos.server

class Preferences {
    String prefKey
    String prefValue
    Year year

    static constraints = {
        prefKey blank: false, size: 1..45, unique: true
        prefValue size: 1..100
    }
}
