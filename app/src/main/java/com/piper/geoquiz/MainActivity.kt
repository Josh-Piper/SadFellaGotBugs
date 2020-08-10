package com.piper.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import java.lang.Exception

private const val TAG = "QuizViewModel"
private const val KEY_INDEX = "index"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton : ImageButton
    private lateinit var questionTextView : TextView
    private lateinit var backButton : ImageButton

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java) }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            val currentIndex = savedInstanceState.getInt(KEY_INDEX, 0)
            quizViewModel.currentIndex = currentIndex
        }

        setContentView(R.layout.activity_main)




        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text)
        questionTextView.setText(quizViewModel.currentQuestionText)
        backButton = findViewById(R.id.back_button)




        trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
            trueButton.isEnabled = false

        }

        falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
            falseButton.isEnabled = false
        }

        nextButton.setOnClickListener { view -> view
            if (checkQuestionsRange(QUESTIONS.increment)) {
                quizViewModel.moveToNext()
            } else {
                Toast.makeText(this, getString(R.string.error_val_text), Toast.LENGTH_SHORT).show()
            }


            updateQuestion()
        }

        questionTextView.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion() }

        backButton.setOnClickListener {
            if (checkQuestionsRange(QUESTIONS.decrement)) {
            } else {
                Toast.makeText(this, getString(R.string.error_val_text), Toast.LENGTH_SHORT).show()
            }
            updateQuestion()
        }


    }

    private fun updateQuestion() {
        var textResID = quizViewModel.currentQuestionText
        questionTextView.setText(textResID)
        falseButton.isEnabled = true
        trueButton.isEnabled = true
    }

    private fun checkAnswer(userAnswer: Boolean) {
        var actualAnswer = quizViewModel.currentQuestionAnswer
        var correctAnswer = userAnswer == actualAnswer
        var areEnabled = falseButton.isEnabled && trueButton.isEnabled
        var showText =
        if (correctAnswer) {
            getString(R.string.correct_text)
        }
        else
        {
            getString(R.string.false_text)
        }
        Toast.makeText(this, showText, Toast.LENGTH_SHORT).show()

        if (correctAnswer && areEnabled)
        {
            quizViewModel.currentAnsweredCorrectly
        }
    }

    private fun checkQuestionsRange(change: QUESTIONS): Boolean {
        if (change == QUESTIONS.increment) {
            if (checkQuestionsIncrement()) {
                return true
            } else  {

                makeText(this, "${quizViewModel.currentAnsweredCorrectly.toDouble() / quizViewModel.sizeOfBank.toDouble() * 100.0}% over the whole quiz", LENGTH_SHORT).show()
                quizViewModel.reset()

                updateQuestion()
                return false
            }
        } else if (change == QUESTIONS.decrement) {
            if (checkQuestionsDecrement()) return true else false
        }
        return false
    }

    private fun checkQuestionsIncrement(): Boolean {
        val newIndex = quizViewModel.currentIndex + 1
        return when {
            newIndex < quizViewModel.sizeOfBank -> true
            else -> false
        }
    }

    private fun checkQuestionsDecrement(): Boolean {
        val newIndex = quizViewModel.currentIndex - 1
        return when {
            newIndex >= 0 -> true
            else -> false
        }
    }

    enum class QUESTIONS {
        decrement,
        increment
    }
}