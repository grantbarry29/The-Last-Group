import android.net.Uri

import android.view.ViewGroup

import android.view.LayoutInflater

import android.content.Context
import android.graphics.Color
import android.view.View

import android.widget.ArrayAdapter
import coil.load
import edu.umich.jakoba.kotlinChatter.R

import edu.umich.jakoba.kotlinChatter.databinding.ListitemSuggestionBinding

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Suggestion(
                 var carName: String? = null,
                 var carCost: String? = null,
                 var probability: String? = null,
                 carImageUri: String? = null) {
    var carImageUri: String? by SuggestionPropDelegate(carImageUri)
}

class SuggestionPropDelegate private constructor ():
    ReadWriteProperty<Any?, String?> {
    private var _value: String? = null
        set(newValue) {
            newValue ?: run {
                field = null
                return
            }
            field = if (newValue == "null" || newValue.isEmpty()) null else newValue
        }

    constructor(initialValue: String?): this() { _value = initialValue }

    override fun getValue(thisRef: Any?, property: KProperty<*>) = _value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) {
        _value = value
    }
}



