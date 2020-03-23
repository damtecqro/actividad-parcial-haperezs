package com.test.pokedex.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.koushikdutta.ion.Ion
import com.test.pokedex.R

import kotlinx.android.synthetic.main.activity_detail.*

class ActivityDetail : AppCompatActivity() {

    private var url: String? = null

    private lateinit var name: TextView
    private lateinit var number: TextView
    private lateinit var types: TextView
    private lateinit var stats: TextView
    private lateinit var moves: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val actionbar = supportActionBar
        actionbar!!.title = "Pokémon Detail"
        actionbar.setDisplayHomeAsUpEnabled(true)

        var intent: Intent = getIntent()
        url = intent.getStringExtra("url")

        name = findViewById(R.id.pokemon_name)
        number = findViewById(R.id.pokemon_number)
        types = findViewById(R.id.pokemon_types)
        stats = findViewById(R.id.pokemon_stats)
        moves = findViewById(R.id.pokemon_moves)

        initializeData()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun initializeData(){
        Ion.with(this)
            .load(url)
            .asJsonObject()
            .done { e, result ->
                if(e == null){
                    val actionbar = supportActionBar
                    actionbar!!.title = result.get("name").toString().replace("\"","").capitalize()
                    name.text = result.get("name").toString().replace("\"","").capitalize()

                    number.text = "#" + result.get("id").toString()
                    types.text = getTypes(result.get("types").asJsonArray)
                    stats.text = getStats(result.get("stats").asJsonArray)
                    moves.text = getMoves(result.get("moves").asJsonArray)


                    if(!result.get("sprites").isJsonNull){
                        if(result.get("sprites").asJsonObject.get("front_default") != null){
                            Log.i("Salida", result.get("sprites").asJsonObject.get("front_default").asString)

                            Glide
                                .with(this)
                                .load(result.get("sprites").asJsonObject.get("front_default").asString)
                                .placeholder(R.drawable.pokemon_logo_min)
                                .error(R.drawable.pokemon_logo_min)
                                .into(pokemon_image)


                        }else{
                            pokemon_image.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pokemon_logo_min))
                        }

                    }else{
                        pokemon_image.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.pokemon_logo_min))
                    }
                }
            }
    }

    fun getTypes(types: JsonArray): String{
        var list: String = ""

        for(item in types){
            var type: JsonObject = item.asJsonObject.getAsJsonObject("type")
            list += type.get("name").asString.capitalize() + "\n"
        }

        return list
    }

    fun getStats(stats: JsonArray): String{
        var list: String = ""

        for(item in stats){
            var value = item.asJsonObject.get("base_stat").asString
            var stat: JsonObject = item.asJsonObject.getAsJsonObject("stat")

            var statShort: String

            when(stat.get("name").asString){
                "special-attack" -> statShort = "Sp. Attack"
                "special-defense" -> statShort ="Sp. Defense"
                else -> statShort = stat.get("name").asString.capitalize()
            }

            list += (statShort + ": " + value + "\n")
        }

        return list
    }

    fun getMoves(moves: JsonArray): String{
        var list: String = ""

        for(item in moves){
            var move: JsonObject = item.asJsonObject.getAsJsonObject("move")
            list += move.get("name").asString.replace("-"," ").capitalize() + "\n"
        }

        return list
    }
}
