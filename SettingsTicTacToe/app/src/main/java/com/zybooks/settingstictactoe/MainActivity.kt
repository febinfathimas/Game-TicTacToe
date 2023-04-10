package com.zybooks.settingstictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.app.AlertDialog
import android.os.Parcel
import android.os.Parcelable
import android.view.ContextMenu
import android.view.MenuItem
import android.widget.Toast.*
import kotlin.system.exitProcess

//import androidx.fragment.app.Fragment

class MainActivity() : AppCompatActivity() , View.OnClickListener, Parcelable {

    private lateinit var buttons: Array<Array<Button>>
    private lateinit var turnTextView: TextView
    private lateinit var scoreBoardTextview: TextView
    private val fragment1btn = findViewById<Button>(R.id.fragment1btn)
    private var playerTurn = 0
    private var count = 0
    private var xWins = 0
    private var oWins = 0



    constructor(parcel: Parcel) : this() {
        playerTurn = parcel.readInt()
        count = parcel.readInt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up buttons
        buttons = arrayOf(
            arrayOf(findViewById(R.id.button_1), findViewById(R.id.button_2), findViewById(R.id.button_3)),
            arrayOf(findViewById(R.id.button_4), findViewById(R.id.button_5), findViewById(R.id.button_6)),
            arrayOf(findViewById(R.id.button_7), findViewById(R.id.button_8), findViewById(R.id.button_9))
        )

        turnTextView = findViewById(R.id.textview2)
        scoreBoardTextview=findViewById(R.id.scoreboard)
        turnTextView.text =getString(R.string.player_x_turn)
        playerTurn = 1

        //registerForContextMenu(turnTextView)
        registerForContextMenu(scoreBoardTextview)

        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j].setOnClickListener(this)
            }
        }

        // Set up reset button
        val resetButton = findViewById<Button>(R.id.button_reset)
        resetButton.setOnClickListener {
            resetGame()
        }

        //setting exit button to exit from app
        val exitButton=findViewById<Button>(R.id.button_exit)
        exitButton.setOnClickListener {
            exitApp()
        }


        // to display the help fragment
       fragment1btn.setOnClickListener {
           val fragment = FragmentHelp()
           val fragmentTransaction = supportFragmentManager.beginTransaction()
           fragmentTransaction.setReorderingAllowed(true)
               .add(R.id.fragmentContainer, fragment)
               .addToBackStack(null)
               .commit()
       }
    }

    /* The code below will terminate the app */
    private fun exitApp() {
        exitProcess(0)
    }




    /* The code below will display X and 0 corresponding to the turn
     for each click in the 9 cells */
    override fun onClick(view: View) {
        if (view !is Button) return
        if (view.text.toString() != "") {
            return
        }
        if (playerTurn == 1) {
            view.text = "X"
            playerTurn = 2
            turnTextView.text =getString(R.string.player_0_turn)
        } else {
            view.text = "O"
            playerTurn = 1
            turnTextView.text = getString(R.string.player_x_turn)
        }

        count++
        checkWinner()
    }

    /* The code below will count X and 0
   to decide the winner or tie */
    private fun checkWinner() {
        var winner = ""

        // Check rows
        for (i in 0..2) {
            if (buttons[i][0].text == buttons[i][1].text && buttons[i][1].text == buttons[i][2].text && buttons[i][0].text.toString() != "") {
                winner = buttons[i][0].text.toString()
                break
            }
        }

        // Check columns
        for (i in 0..2) {
            if (buttons[0][i].text == buttons[1][i].text && buttons[1][i].text == buttons[2][i].text && buttons[0][i].text.toString() != "") {
                winner = buttons[0][i].text.toString()
                break
            }
        }

        // Check diagonals
        if (buttons[0][0].text == buttons[1][1].text && buttons[1][1].text == buttons[2][2].text && buttons[0][0].text.toString() != "") {
            winner = buttons[0][0].text.toString()
        } else if (buttons[0][2].text == buttons[1][1].text && buttons[1][1].text == buttons[2][0].text && buttons[0][2].text.toString() != "") {
            winner = buttons[0][2].text.toString()
        }

        //checking the game status to display AlertDialog that has dialog buttons
        if (winner != "") {
            showWinnerDialog(winner)
            disableButtons()
        } else if (count == 9) {
            showTieDialog()
        }
    }

    /* The code below will display
    AlertDialog that has dialog buttons EXIT and continue*/
    private fun showTieDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Game Tied ")
        alertDialogBuilder.setMessage("It's a tie! no one scored \n check the scoreboard before exiting or continue")
        alertDialogBuilder.setPositiveButton("Continue Game") { _, _ ->
            resetGame()
        }
        alertDialogBuilder.setNegativeButton("Exit GameApp") { _, _ ->
            finish()
        }
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }



    private fun disableButtons() {
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j].isEnabled = false
            }
        }
    }

    //AlertDialog that has dialog buttons
    private fun showWinnerDialog(winner: String) {
        // Increment xWins or oWins based on the winner
        if (winner == "X") {
            xWins++
        } else {
            oWins++
        }

        // Show the winner dialog
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Game Over")
        alertDialogBuilder.setMessage("Player $winner scored! \n check the scoreboard before exiting or continue")
        alertDialogBuilder.setPositiveButton("Game Continue") { _, _ ->
            resetGame()
        }
        alertDialogBuilder.setNegativeButton("Exit GameApp") { _, _ ->
            finish()
        }
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.show()
    }

    /* The code below will reset the
    game-board to restart the game again*/
    private fun resetGame() {
        for (i in 0..2) {
            for (j in 0..2) {
                buttons[i][j].text = ""
                buttons[i][j].isEnabled = true
            }
        }
        turnTextView.text =getString(R.string.player_x_turn)
        playerTurn = 1
        count = 0
    }

    /* The code below will display the context menu to scoreboard*/
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menuInflater.inflate(R.menu.context_menu,menu)
    }

    /* The code below will handle the option selection inside the context menu*/
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.option_1->{
                makeText(applicationContext,"X Wins: $xWins", LENGTH_SHORT).show()
                return true}
            R.id.option_2->{
                makeText(applicationContext,"O Wins: $oWins", LENGTH_SHORT).show()
                return true}

            else -> {super.onContextItemSelected(item)}
        }

    }

    /* supporting functions to make text*/
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(playerTurn)
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

}


