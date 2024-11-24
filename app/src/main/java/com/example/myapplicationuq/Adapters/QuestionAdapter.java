package com.example.myapplicationuq.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private static final int VIEW_TYPE_SUBMIT_BUTTON = 5;

    private List<QuestionResponse> questionList;
    private Context context;

    // Map to store user answers: key = questionID, value = user answer
    private Map<Integer, Object> userAnswers = new HashMap<>();

    // Set to store incorrect question IDs
    private Set<Integer> incorrectQuestionIDs = new HashSet<>();

    // Flag to indicate if the quiz has been submitted
    private boolean isSubmitted = false;

    // Constructor
    public QuestionAdapter(Context context, List<QuestionResponse> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= questionList.size())
            return VIEW_TYPE_SUBMIT_BUTTON;
        return questionList.get(position).getQuestionType();
    }

    @Override
    public int getItemCount() {
        return questionList.size() + 1;
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
        } else if (viewType == VIEW_TYPE_SUBMIT_BUTTON) {
            View view = inflater.inflate(R.layout.item_submit_button, parent, false);
            return new SubmitButtonViewHolder(view);
        } else {
            // Default case
            View view = inflater.inflate(R.layout.item_question_single_choice, parent, false);
            return new SingleChoiceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SubmitButtonViewHolder) {
            ((SubmitButtonViewHolder) holder).bind();
        } else {
            // Ensure position is within bounds
            if (position < questionList.size()) {
                QuestionResponse question = questionList.get(position);
                boolean isIncorrect = incorrectQuestionIDs.contains(question.getQuestionID());
                if (holder instanceof SingleChoiceViewHolder) {
                    ((SingleChoiceViewHolder) holder).bind(question, isIncorrect);
                } else if (holder instanceof TrueFalseViewHolder) {
                    ((TrueFalseViewHolder) holder).bind(question, isIncorrect);
                } else if (holder instanceof MultipleChoiceViewHolder) {
                    ((MultipleChoiceViewHolder) holder).bind(question, isIncorrect);
                } else if (holder instanceof SingleAnswerViewHolder) {
                    ((SingleAnswerViewHolder) holder).bind(question, isIncorrect);
                }
            }
        }
    }

    public interface OnSubmitClickListener {
        void onSubmitClicked();
    }

    private OnSubmitClickListener onSubmitClickListener;

    public void setOnSubmitClickListener(OnSubmitClickListener listener) {
        this.onSubmitClickListener = listener;
    }

    // ViewHolder for Submit Button
    public class SubmitButtonViewHolder extends RecyclerView.ViewHolder {

        Button submitButton;

        public SubmitButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            submitButton = itemView.findViewById(R.id.buttonSubmit);
        }

        public void bind() {
            submitButton.setOnClickListener(v -> {
                if (onSubmitClickListener != null) {
                    onSubmitClickListener.onSubmitClicked();
                }
            });
        }
    }

    // ViewHolder for Single Choice Questions
    public class SingleChoiceViewHolder extends RecyclerView.ViewHolder {

        TextView textViewQuestionText;
        RadioGroup radioGroupChoices;
        View rootLayout;
        public SingleChoiceViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestionText = itemView.findViewById(R.id.textViewQuestionText);
            radioGroupChoices = itemView.findViewById(R.id.radioGroupChoices);
            rootLayout = itemView.findViewById(R.id.rootLayout);

        }

        public void bind(QuestionResponse question, boolean isIncorrect) {
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

                // Disable input if submitted
                if (isSubmitted) {
                    radioButton.setEnabled(false);
                }
            }

            // Handle selection changes
            if (!isSubmitted) {
                radioGroupChoices.setOnCheckedChangeListener((group, checkedId) -> {
                    // Find the selected choiceID
                    RadioButton selectedRadioButton = group.findViewById(checkedId);
                    int index = group.indexOfChild(selectedRadioButton);
                    ChoiceResponse selectedChoice = question.getChoices().get(index);

                    // Save the selected choiceID
                    userAnswers.put(question.getQuestionID(), selectedChoice.getChoiceID());
                });
            } else {
                radioGroupChoices.setOnCheckedChangeListener(null);
            }

            // Highlight if incorrect
            if (isIncorrect) {
                rootLayout.setBackgroundColor(context.getResources().getColor(R.color.incorrect_answer_background));
            } else {
                rootLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
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

        public void bind(QuestionResponse question, boolean isIncorrect) {
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

            // Disable input if submitted
            if (isSubmitted) {
                radioButtonTrue.setEnabled(false);
                radioButtonFalse.setEnabled(false);
                radioGroupTrueFalse.setOnCheckedChangeListener(null);
            } else {
                // Handle selection changes
                radioGroupTrueFalse.setOnCheckedChangeListener((group, checkedId) -> {
                    boolean selectedAnswer = checkedId == radioButtonTrue.getId();
                    userAnswers.put(question.getQuestionID(), selectedAnswer);
                });
            }

            // Highlight if incorrect
            if (isIncorrect) {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.incorrect_answer_background));
            } else {
                itemView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
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

        @SuppressLint("ResourceAsColor")
        public void bind(QuestionResponse question, boolean isIncorrect) {
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

                // Disable input if submitted
                if (isSubmitted) {
                    checkBox.setEnabled(false);
                    checkBox.setOnCheckedChangeListener(null);
                } else {
                    // Handle checkbox changes
                    checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
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
                    });
                }
            }

            // Highlight if incorrect
            if (isIncorrect) {
                itemView.setBackgroundColor(R.color.incorrect_answer_background);
            } else {
                itemView.setBackgroundColor(R.color.gray);
            }
        }
    }

    // ViewHolder for Single Answer Questions
    public class SingleAnswerViewHolder extends RecyclerView.ViewHolder {

        TextView textViewQuestionText;
        EditText editTextAnswer;
        View rootLayout;


        public SingleAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewQuestionText = itemView.findViewById(R.id.textViewQuestionText);
            editTextAnswer = itemView.findViewById(R.id.editTextAnswer);
            rootLayout = itemView.findViewById(R.id.rootLayout);
        }

        public void bind(QuestionResponse question, boolean isIncorrect) {
            textViewQuestionText.setText(question.getQuestionText());

            // Remove any existing TextWatcher to prevent multiple instances
            if (editTextAnswer.getTag() instanceof TextWatcher) {
                editTextAnswer.removeTextChangedListener((TextWatcher) editTextAnswer.getTag());
            }

            // Restore previous input
            Object answer = userAnswers.get(question.getQuestionID());
            if (answer != null) {
                editTextAnswer.setText((String) answer);
            } else {
                editTextAnswer.setText(""); // Clear previous text
            }

            // Disable input if submitted
            if (isSubmitted) {
                editTextAnswer.setEnabled(false);
            } else {
                editTextAnswer.setEnabled(true);

                // Handle text input
                TextWatcher textWatcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Optional
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Optional
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        userAnswers.put(question.getQuestionID(), s.toString());
                    }
                };

                editTextAnswer.addTextChangedListener(textWatcher);

                // Store the TextWatcher as a tag so we can remove it later
                editTextAnswer.setTag(textWatcher);
            }

            // Highlight if incorrect
            if (isIncorrect) {
                rootLayout.setBackgroundColor(context.getResources().getColor(R.color.incorrect_answer_background));
            } else {
                rootLayout.setBackgroundColor(context.getResources().getColor(R.color.white)); // Or R.color.gray if desired
            }
        }
    }


    // Method to retrieve user answers
    public Map<Integer, Object> getUserAnswers() {
        return userAnswers;
    }

    // Method to set incorrect question IDs and update UI
    public void setIncorrectQuestionIDs(Set<Integer> incorrectQuestionIDs) {
        this.incorrectQuestionIDs = incorrectQuestionIDs;
        notifyDataSetChanged();
    }

    // Method to set the submitted state and update UI
    public void setSubmitted(boolean submitted) {
        this.isSubmitted = submitted;
        notifyDataSetChanged();
    }
}
