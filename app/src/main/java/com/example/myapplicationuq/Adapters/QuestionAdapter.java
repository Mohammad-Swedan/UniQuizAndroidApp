package com.example.myapplicationuq.Adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplicationuq.R;
import com.example.myapplicationuq.Responses.ChoiceResponse;
import com.example.myapplicationuq.Responses.QuestionResponse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_SINGLE_CHOICE = 1;
    public static final int TYPE_TRUE_FALSE = 2;
    public static final int TYPE_MULTIPLE_CHOICE = 3;
    public static final int TYPE_SINGLE_ANSWER = 4;

    private List<QuestionResponse> questionList;
    private Context context;

    // Map to store user answers: key = questionID, value = user answer
    private Map<Integer, Object> userAnswers = new HashMap<>();

    // Constructor
    public QuestionAdapter(Context context, List<QuestionResponse> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @Override
    public int getItemViewType(int position) {
        return questionList.get(position).getQuestionType();
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == TYPE_SINGLE_CHOICE) {
            View view = inflater.inflate(R.layout.item_question_single_choice, parent, false);
            return new SingleChoiceViewHolder(view);
        } else if (viewType == TYPE_TRUE_FALSE) {
            View view = inflater.inflate(R.layout.item_question_true_false, parent, false);
            return new TrueFalseViewHolder(view);
        } else if (viewType == TYPE_MULTIPLE_CHOICE) {
            View view = inflater.inflate(R.layout.item_question_multiple_choice, parent, false);
            return new MultipleChoiceViewHolder(view);
        } else if (viewType == TYPE_SINGLE_ANSWER) {
            View view = inflater.inflate(R.layout.item_question_single_answer, parent, false);
            return new SingleAnswerViewHolder(view);
        } else {
            // Default case
            View view = inflater.inflate(R.layout.item_question_single_choice, parent, false);
            return new SingleChoiceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        QuestionResponse question = questionList.get(position);

        if (holder instanceof SingleChoiceViewHolder) {
            ((SingleChoiceViewHolder) holder).bind(question);
        } else if (holder instanceof TrueFalseViewHolder) {
            ((TrueFalseViewHolder) holder).bind(question);
        } else if (holder instanceof MultipleChoiceViewHolder) {
            ((MultipleChoiceViewHolder) holder).bind(question);
        } else if (holder instanceof SingleAnswerViewHolder) {
            ((SingleAnswerViewHolder) holder).bind(question);
        }
    }

    // ViewHolder for Single Choice Questions
    public class SingleChoiceViewHolder extends RecyclerView.ViewHolder {

        TextView textViewQuestionText;
        RadioGroup radioGroupChoices;

        public SingleChoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestionText = itemView.findViewById(R.id.textViewQuestionText);
            radioGroupChoices = itemView.findViewById(R.id.radioGroupChoices);
        }

        public void bind(QuestionResponse question) {
            textViewQuestionText.setText(question.getQuestionText());

            // Clear previous RadioButtons
            radioGroupChoices.removeAllViews();

            // Dynamically add RadioButtons
            for (ChoiceResponse choice : question.getChoices()) {
                RadioButton radioButton = new RadioButton(context);
                radioButton.setText(choice.getChoiceText());
                radioButton.setId(View.generateViewId());
                radioGroupChoices.addView(radioButton);

                // Restore previous selection
                Object answer = userAnswers.get(question.getQuestionID());
                if (answer != null && answer.equals(choice.getChoiceID())) {
                    radioButton.setChecked(true);
                }
            }

            // Handle selection changes
            radioGroupChoices.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    // Find the selected choiceID
                    RadioButton selectedRadioButton = group.findViewById(checkedId);
                    int index = group.indexOfChild(selectedRadioButton);
                    ChoiceResponse selectedChoice = question.getChoices().get(index);

                    // Save the selected choiceID
                    userAnswers.put(question.getQuestionID(), selectedChoice.getChoiceID());
                }
            });
        }
    }

    // ViewHolder for True or False Questions
    public class TrueFalseViewHolder extends RecyclerView.ViewHolder {

        TextView textViewQuestionText;
        RadioGroup radioGroupTrueFalse;
        RadioButton radioButtonTrue, radioButtonFalse;

        public TrueFalseViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestionText = itemView.findViewById(R.id.textViewQuestionText);
            radioGroupTrueFalse = itemView.findViewById(R.id.radioGroupTrueFalse);
            radioButtonTrue = itemView.findViewById(R.id.radioButtonTrue);
            radioButtonFalse = itemView.findViewById(R.id.radioButtonFalse);
        }

        public void bind(QuestionResponse question) {
            textViewQuestionText.setText(question.getQuestionText());

            // Restore previous selection
            Object answer = userAnswers.get(question.getQuestionID());
            if (answer != null) {
                if (answer.equals(true)) {
                    radioButtonTrue.setChecked(true);
                } else if (answer.equals(false)) {
                    radioButtonFalse.setChecked(true);
                }
            }

            // Handle selection changes
            radioGroupTrueFalse.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    boolean selectedAnswer = checkedId == R.id.radioButtonTrue;
                    userAnswers.put(question.getQuestionID(), selectedAnswer);
                }
            });
        }
    }

    // ViewHolder for Multiple Choice Questions
    public class MultipleChoiceViewHolder extends RecyclerView.ViewHolder {

        TextView textViewQuestionText;
        LinearLayout linearLayoutChoices;

        public MultipleChoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestionText = itemView.findViewById(R.id.textViewQuestionText);
            linearLayoutChoices = itemView.findViewById(R.id.linearLayoutMultipleChoices);
        }

        public void bind(QuestionResponse question) {
            textViewQuestionText.setText(question.getQuestionText());

            // Remove existing CheckBoxes
            linearLayoutChoices.removeAllViews();

            // Dynamically add CheckBoxes
            for (ChoiceResponse choice : question.getChoices()) {
                CheckBox checkBox = new CheckBox(context);
                checkBox.setText(choice.getChoiceText());
                checkBox.setId(View.generateViewId());
                linearLayoutChoices.addView(checkBox);

                // Restore previous selections
                Object answer = userAnswers.get(question.getQuestionID());
                if (answer != null && answer instanceof Set) {
                    Set<Integer> selectedChoices = (Set<Integer>) answer;
                    if (selectedChoices.contains(choice.getChoiceID())) {
                        checkBox.setChecked(true);
                    }
                }

                // Handle checkbox changes
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Set<Integer> selectedChoices = (Set<Integer>) userAnswers.get(question.getQuestionID());
                        if (selectedChoices == null) {
                            selectedChoices = new HashSet<>();
                        }
                        if (isChecked) {
                            selectedChoices.add(choice.getChoiceID());
                        } else {
                            selectedChoices.remove(choice.getChoiceID());
                        }
                        userAnswers.put(question.getQuestionID(), selectedChoices);
                    }
                });
            }
        }
    }

    // ViewHolder for Single Answer Questions
    public class SingleAnswerViewHolder extends RecyclerView.ViewHolder {

        TextView textViewQuestionText;
        EditText editTextAnswer;

        public SingleAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestionText = itemView.findViewById(R.id.textViewQuestionText);
            editTextAnswer = itemView.findViewById(R.id.editTextAnswer);
        }

        public void bind(QuestionResponse question) {
            textViewQuestionText.setText(question.getQuestionText());

            // Restore previous input
            Object answer = userAnswers.get(question.getQuestionID());
            if (answer != null) {
                editTextAnswer.setText((String) answer);
            }

            // Handle text input
            editTextAnswer.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // Optional
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    userAnswers.put(question.getQuestionID(), s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Optional
                }
            });
        }
    }

    // Method to retrieve user answers
    public Map<Integer, Object> getUserAnswers() {
        return userAnswers;
    }
}
