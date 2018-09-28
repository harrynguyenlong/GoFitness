package fi.metropolia.harrytoan.gofitness

import android.location.Location

class Candy {
    var name:String? = null
    var des:String? = null
    var image:Int? = null
    var amount:Double? = null
    var location: Location? = null
    var IsCatch:Boolean? = false
    constructor(image:Int, name:String, des:String, amount:Double, lat:Double, log:Double){
        this.name = name
        this.des = des
        this.image = image
        this.amount = amount
        this.location = Location(name)
        this.location!!.latitude= lat
        this.location!!.longitude= log
        this.IsCatch = false
    }
}