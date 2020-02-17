package com.lougw.calculate;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lougw.calculate.utils.UIUtils;
import com.lougw.calculate.utils.adapter.BaseRecyclerAdapter;
import com.lougw.calculate.utils.adapter.BaseRecyclerViewHolder;
import com.lougw.calculate.utils.adapter.OnItemClickListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, OnItemClickListener {
    private static final String TAG = "CalculateActivity";
    private static final Pattern NUMBER_PATTERN_INT = Pattern.compile("^-?[1-9]\\d*$");
    private static final Pattern NUMBER_PATTERN_FLOAT = Pattern.compile("^-?([1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*|0?\\.0+|0)$");
    private static final NumberFormat DECIMAL_FORMAT = new DecimalFormat("#.#");

    private RadioGroup algorithm_rg;
    private RadioButton plus_rb;
    private RadioButton minus_rb;
    private RadioButton multiply_rb;
    private RadioButton divide_rb;
    private RadioButton mix_rb;
    private Button exam;
    private Button confirm;
    private RecyclerView mRecyclerView;
    private BaseRecyclerAdapter mBaseRecyclerAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private SeekBar range_sb;
    private TextView range;
    private SeekBar num_sb;
    private TextView num;
    private TextView time;
    private long lastTime = 0;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 1000);
            long current = System.currentTimeMillis();
            time.setText(doFormatTimer((current - (lastTime == 0 ? current : lastTime)) / 1000));
        }
    };

    public static boolean isNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return NUMBER_PATTERN_INT.matcher(str).find() || NUMBER_PATTERN_FLOAT.matcher(str).find();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        algorithm_rg = findViewById(R.id.algorithm_rg);
        algorithm_rg.setOnCheckedChangeListener(this);
        exam = findViewById(R.id.exam);
        range_sb = findViewById(R.id.range_sb);
        plus_rb = findViewById(R.id.plus_rb);
        minus_rb = findViewById(R.id.minus_rb);
        multiply_rb = findViewById(R.id.multiply_rb);
        divide_rb = findViewById(R.id.divide_rb);
        mix_rb = findViewById(R.id.mix_rb);
        range = findViewById(R.id.range);
        num = findViewById(R.id.num);
        num_sb = findViewById(R.id.num_sb);
        time = findViewById(R.id.time);
        range_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                range.setText("数字范围(" + setRangeSeekBar(seekBar, false) + ")");

            }
        });
        num_sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                num.setText("出题个数(" + setRangeSeekBar(seekBar, true) + ")");

            }
        });
        confirm = findViewById(R.id.confirm);
        exam.setOnClickListener(this);
        confirm.setOnClickListener(this);
        mRecyclerView = findViewById(R.id.recycler_view);
        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mBaseRecyclerAdapter = new BaseRecyclerAdapter(this) {
            @Override
            public BaseRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new CalculateViewHolder(LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_calculate_view, parent, false));
            }
        };
        mRecyclerView.setAdapter(mBaseRecyclerAdapter);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        mBaseRecyclerAdapter.clear();
        doStopTiming();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.exam) {
            doExam();
        } else if (v.getId() == R.id.confirm) {
            confirm();
        }

    }

    private void doExam() {
        ArrayList<CalculateBean> data = new ArrayList();
        int progress = range_sb.getProgress();
        int num = num_sb.getProgress();
        for (int i = 0; i < num; i++) {
            CalculateBean bean = new CalculateBean();
            if (plus_rb.isChecked()) {
                doPackBeanPlus(bean, progress);
            } else if (minus_rb.isChecked()) {
                doPackBeanMinus(bean, progress);
            } else if (multiply_rb.isChecked()) {
                doPackBeanMultiply(bean, progress);
            } else if (divide_rb.isChecked()) {
                doPackBeanDivide(bean, progress);
            } else if (mix_rb.isChecked()) {
                int algorithmic = new Random().nextInt(4);
                if (algorithmic == 0) {
                    doPackBeanPlus(bean, progress);
                } else if (algorithmic == 1) {
                    doPackBeanMinus(bean, progress);
                } else if (algorithmic == 2) {
                    doPackBeanMultiply(bean, progress);
                } else if (algorithmic == 3) {
                    doPackBeanDivide(bean, progress);
                }

            }
            data.add(bean);
        }
        mBaseRecyclerAdapter.setData(data);
        doStartTiming();
    }

    private void doPackBeanPlus(CalculateBean bean, int progress) {
        bean.setAlgorithm(Algorithm.PLUS);
        bean.setFirstNum(new Random().nextInt(progress));
        bean.setSecondNum(new Random().nextInt(progress));

    }

    private void doPackBeanMinus(CalculateBean bean, int progress) {
        bean.setAlgorithm(Algorithm.MINUS);
        int first = new Random().nextInt(progress);
        int second = new Random().nextInt(progress);
        if (first < second) {
            int temp = first;
            first = second;
            second = temp;
        }
        bean.setFirstNum(first);
        bean.setSecondNum(second);
    }

    private void doPackBeanMultiply(CalculateBean bean, int progress) {
        bean.setAlgorithm(Algorithm.MULTIPLY);
        bean.setFirstNum(new Random().nextInt(progress));
        bean.setSecondNum(new Random().nextInt(progress));
    }

    private void doPackBeanDivide(CalculateBean bean, int progress) {
        bean.setAlgorithm(Algorithm.DIVIDE);
        int first = new Random().nextInt(progress);
        int second = new Random().nextInt(progress) + 1;
        first = (first == 0) ? 1 : first;
        second = (second == 0) ? 1 : second;
        int temp = first * second;
        bean.setFirstNum(temp);
        bean.setSecondNum(first);
    }

    private int setRangeSeekBar(SeekBar seekBar, boolean num) {
        int progress = seekBar.getProgress();
        int tempProgress = progress;
        if (progress < 10) {
            if (progress < 2) {
                tempProgress = 1;
            } else if (progress >= 2 && progress < 6) {
                tempProgress = 5;
            } else if (progress >= 6) {
                tempProgress = 10;
            }
        } else if (progress >= 10 && progress < 15) {
            tempProgress = 10;
        } else if (progress >= 15 && progress < 25) {
            tempProgress = 20;
        } else if (progress >= 25 && progress < 35) {
            tempProgress = 30;
        } else if (progress >= 35 && progress < 45) {
            tempProgress = 40;
        } else if (progress >= 45 && progress < 55) {
            tempProgress = 50;
        } else if (progress >= 55 && progress < 65) {
            tempProgress = 60;
        } else if (progress >= 65 && progress < 75) {
            tempProgress = 70;
        } else if (progress >= 75 && progress < 85) {
            tempProgress = 80;
        } else if (progress >= 85 && progress < 95) {
            tempProgress = 90;
        } else if (progress >= 95 && progress <= 100) {
            tempProgress = 100;
        }
        seekBar.setProgress(tempProgress);
        return tempProgress;
    }


    private void confirm() {
        int firstErrorPosition = 0;
        boolean hasError = false;
        ArrayList<CalculateBean> data = (ArrayList<CalculateBean>) mBaseRecyclerAdapter.getData();
        int size = data.size();
        for (int i = 0; i < size; i++) {
            CalculateBean bean = data.get(i);
            if (bean.getAlgorithm() == Algorithm.PLUS) {
                if (bean.getFirstNum() + bean.getSecondNum() == bean.getResult()) {
                    bean.setState(AnswerState.SUCCESS_ANSWERED);
                } else {
                    bean.setState(AnswerState.ERROR_ANSWERED);
                    if (!hasError) {
                        firstErrorPosition = i;
                    }
                    hasError = true;
                }
            } else if (bean.getAlgorithm() == Algorithm.MINUS) {
                if (bean.getFirstNum() - bean.getSecondNum() == bean.getResult()) {
                    bean.setState(AnswerState.SUCCESS_ANSWERED);
                } else {
                    bean.setState(AnswerState.ERROR_ANSWERED);
                    if (!hasError) {
                        firstErrorPosition = i;
                    }
                    hasError = true;
                }

            } else if (bean.getAlgorithm() == Algorithm.MULTIPLY) {
                if (bean.getFirstNum() * bean.getSecondNum() == bean.getResult()) {
                    bean.setState(AnswerState.SUCCESS_ANSWERED);
                } else {
                    bean.setState(AnswerState.ERROR_ANSWERED);
                    if (!hasError) {
                        firstErrorPosition = i;
                    }
                    hasError = true;
                }
            } else if (bean.getAlgorithm() == Algorithm.DIVIDE) {
                Log.d(TAG, "confirm: " + Float.parseFloat(DECIMAL_FORMAT.format(bean.getFirstNum() * 1.0 / bean.getSecondNum())) + " " + bean.getResult());
                if (Float.parseFloat(DECIMAL_FORMAT.format(bean.getFirstNum() * 1.0 / bean.getSecondNum())) == bean.getResult()) {
                    bean.setState(AnswerState.SUCCESS_ANSWERED);
                } else {
                    bean.setState(AnswerState.ERROR_ANSWERED);
                    if (!hasError) {
                        firstErrorPosition = i;
                    }
                    hasError = true;
                }
            }

        }
        if (hasError) {
            mRecyclerView.scrollToPosition(firstErrorPosition);
            Toast.makeText(getApplicationContext(), "答题有错误，请修改后再提交", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "恭喜你答了100分", Toast.LENGTH_LONG).show();
            doStopTiming();
        }
        mBaseRecyclerAdapter.notifyItemRangeChanged(0, size);
    }

    static class CalculateViewHolder extends BaseRecyclerViewHolder<CalculateBean> {
        AppCompatTextView first_num;
        AppCompatTextView arithmetic;
        AppCompatTextView second_num;
        AppCompatEditText result_tv;
        AppCompatTextView result_state;

        public CalculateViewHolder(View view) {
            super(view);
            result_state = view.findViewById(R.id.result_state);
            first_num = view.findViewById(R.id.first_num);
            arithmetic = view.findViewById(R.id.arithmetic);
            second_num = view.findViewById(R.id.second_num);
            result_tv = view.findViewById(R.id.result_tv);
            result_tv.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String str = s.toString();
                    if (TextUtils.isEmpty(str)) {
                        CalculateBean bean = getData();
                        if (bean != null) {
                            bean.setResult(-999);
                        }
                    } else {
                        CalculateBean bean = getData();
                        if (bean != null) {
                            if (isNumber(str)) {
                                if (str.contains(".")) {
                                    bean.setResult(Float.parseFloat(str));
                                } else {
                                    bean.setResult(Integer.parseInt(str));
                                }
                            } else {
                                bean.setResult(0);
                            }
                            Log.d(TAG, "refreshView: " + bean.getResult());
                        }
                    }

                }
            });
        }

        @Override
        public void onDataBinding(int position) {
            itemView.setBackgroundColor(getAdapterPosition() / 2 == 0
                    ? UIUtils.getColor(R.color.transparent_per_5_white)
                    : UIUtils.getColor(R.color.transparent_per_10_white));
            CalculateBean bean = getData();
            if (bean.getAlgorithm() == Algorithm.PLUS) {
                arithmetic.setText("+");
            } else if (bean.getAlgorithm() == Algorithm.MINUS) {
                arithmetic.setText("-");
            } else if (bean.getAlgorithm() == Algorithm.MULTIPLY) {
                arithmetic.setText("*");
            } else if (bean.getAlgorithm() == Algorithm.DIVIDE) {
                arithmetic.setText("/");
            }
            first_num.setText(String.valueOf(bean.getFirstNum()));
            second_num.setText(String.valueOf(bean.getSecondNum()));

            result_tv.setText(String.valueOf(bean.getResult() == -999 ? "" : DECIMAL_FORMAT.format(bean.getResult())));
            if (bean.getState() == AnswerState.ERROR_ANSWERED) {
                result_state.setVisibility(View.VISIBLE);
            } else {
                result_state.setVisibility(View.INVISIBLE);
            }
            Log.d(TAG, "refreshView: " + bean.getResult());
        }

        @Override
        public void recycle() {

        }
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    private String doFormatTimer(long time) {
        final int MINUTE = 60;
        final int HOUR = 60 * 60;
        return (int) time / HOUR + ":" + (int) time % HOUR / MINUTE + ":" + time % MINUTE;
    }

    private void doStartTiming() {
        lastTime = System.currentTimeMillis();
        mHandler.removeCallbacks(mRunnable);
        mHandler.postDelayed(mRunnable, 1000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        doStopTiming();
    }

    private void doStopTiming() {
        mHandler.removeCallbacks(mRunnable);
    }
}