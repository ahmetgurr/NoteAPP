package com.example.noteapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteapp.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.zip.Inflater

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var notesList: ArrayList<Notes>
    private lateinit var adapter: NoteAdapter
    private lateinit var refNotes: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Not Uygulaması"




        setSupportActionBar(binding.toolbar)

        binding.rv.layoutManager = LinearLayoutManager(this)

        val db = FirebaseDatabase.getInstance()
        refNotes = db.getReference("kisiler")


        notesList = ArrayList()


        adapter = NoteAdapter(this,notesList,refNotes)
        binding.rv.adapter = adapter
        tumKisiler()

        binding.imageViewEkle.setOnClickListener {
            alertGoster()

        }
    }

    fun alertGoster(){
        val tasarim =LayoutInflater.from(this).inflate(R.layout.alert_design,null)
        val editTextBaslik = tasarim.findViewById(R.id.editTextBaslik) as EditText
        val editTextIcerik = tasarim.findViewById(R.id.editTextIcerik) as EditText

        val ad = AlertDialog.Builder(this)
        ad.setTitle("Not Ekle")
        ad.setView(tasarim)
        ad.setPositiveButton("Ekle"){dialogInterface,i->
            val notBaslik =editTextBaslik.text.toString().trim()
            val notIcerik =editTextIcerik.text.toString().trim()// trim baştaki ve sondaki boşlukları kaldırır

            val not = Notes("",notBaslik,notIcerik)

            refNotes.push().setValue(not)

            Toast.makeText(applicationContext,"$notBaslik-$notIcerik", Toast.LENGTH_SHORT).show()
        }
        ad.setNegativeButton("İptal"){dialogInterface,i->
        }
        ad.create().show()
    }

    fun tumKisiler(){
        refNotes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()

                for (c in snapshot.children){
                    val kisi = c.getValue(Notes::class.java)

                    if (kisi != null){
                        kisi.note_id = c.key
                        notesList.add(kisi)
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

}