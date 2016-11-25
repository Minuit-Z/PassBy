package Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.ant.liao.GifView;
import com.ziye.passby.R;
import com.ziye.passby.SettingsActivity;

import java.text.DecimalFormat;
import java.util.Calendar;

import services.StepCounterService;
import utils.StepDetector;

public class StepsFragment extends Fragment {
    private TextView tv_show_step;// 步数展示
    private TextView tv_week_day;// 周数
    private TextView tv_date;// 日期天
    private TextView tv_timer;// 所用时间
    private TextView tv_distance;//	行程距离
    private TextView tv_calories;// 卡路里
    private TextView tv_velocity;// 速度

    private Button btn_start;// 开始按钮
    private Button btn_stop;// 结束按钮

    private GifView gifView; //gif图片
    private boolean isRun = false;//判断是否开始

    // 十颗星标
    private ImageView iv_star_1;
    private ImageView iv_star_2;
    private ImageView iv_star_3;
    private ImageView iv_star_4;
    private ImageView iv_star_5;
    private ImageView iv_star_6;
    private ImageView iv_star_7;
    private ImageView iv_star_8;
    private ImageView iv_star_9;
    private ImageView iv_star_10;

    private long timer = 0;// 时间变量
    private long startTimer = 0;

    private long tempTime = 0;

    private Double distance = 0.0;// 步行距离
    private Double calories = 0.0;// 步行卡路里
    private Double velocity = 0.0;// 步行速度

    private int step_length = 0;  //步长
    private int weight = 0;       //重量
    private int total_step = 0;   //总步数

    private Thread thread;//定义线程对象

    private TableRow hide1, hide2;
    private TextView step_counter;

    // 当创建一个新的Handler实例时, 它会绑定到当前线程和消息的队列中,开始分发数据
    // Handler有两个作用, (1) : 定时执行Message和Runnalbe 对象
    // (2): 让一个动作,在不同的线程中执行.
    Handler handler = new Handler() {// Handler对象用于更新当前步数,定时发送消息，调用方法查询数据用于显示
        //主要接受子线程发送的数据, 并用此数据配合主线程更新UI
        //Handler运行在主线程中(UI线程中), 它与子线程可以通过Message对象来传递数据,
        //Handler就承担着接受子线程传过来的(子线程用sendMessage()方法传递Message对象，(里面包含数据)
        //把这些消息放入主线程队列中，配合主线程进行更新UI。
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);  // 此处可以更新UI

            countDistance();//调用距离方法，看一下走了多远

            if (timer != 0 && distance != 0.0) {

                // 体重、距离
                // 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036
                calories = weight * distance * 0.001;
                //速度计算公式
                velocity = distance * 1000 / timer;
            } else {
                calories = 0.0;
                velocity = 0.0;
            }
            countStep();//调用步数方法
            tv_show_step.setText(total_step + "");// 显示当前步数
            tv_distance.setText(formatDouble(distance));// 显示路程
            tv_calories.setText(formatDouble(calories));// 显示卡路里
            tv_velocity.setText(formatDouble(velocity));// 显示速度
            tv_timer.setText(getFormatTime(timer));// 显示当前运行时间
            changeStep();// 设置当前步数和星标
        }
    };

    View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.main, container, false);


        //获取是否在走路
//        Bundle extras = getIntent().getExtras();
        isRun = false;
        if (!isRun) {
            //走路
            gifView = (GifView) rootView.findViewById(R.id.gif_view);
            gifView.setGifImageType(GifView.GifImageType.COVER);
            gifView.setShowDimension(100, 100);
            gifView.setGifImage(R.drawable.run_gif);
            gifView.showCover();
        }
        if (thread == null) {
            thread = new Thread() {// 子线程用于监听当前步数的变化
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    int temp = 0;
                    while (true) {
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (StepCounterService.FLAG) {
                            Message msg = new Message();
                            if (temp != StepDetector.CURRENT_SETP) {
                                temp = StepDetector.CURRENT_SETP;
                            }
                            if (startTimer != System.currentTimeMillis()) {
                                timer = tempTime + System.currentTimeMillis()
                                        - startTimer;
                            }
                            handler.sendMessage(msg);// 通知主线程
                        }
                    }
                }
            };
            thread.start();
        }


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("APP", "on resuame.");
        // 获取界面控件
        addView(rootView);

        // 初始化控件
        init();
    }

    /**
     * 计算行走的距离
     */
    private void countDistance() {
        if (StepDetector.CURRENT_SETP % 2 == 0) {
            distance = (StepDetector.CURRENT_SETP / 2) * 3 * step_length * 0.01;
        } else {
            distance = ((StepDetector.CURRENT_SETP / 2) * 3 + 1) * step_length * 0.01;
        }
    }

    /**
     * 实际的步数
     */
    private void countStep() {
        if (StepDetector.CURRENT_SETP % 2 == 0) {
            total_step = StepDetector.CURRENT_SETP;
        } else {
            total_step = StepDetector.CURRENT_SETP + 1;
        }
        total_step = StepDetector.CURRENT_SETP;
    }

    /**
     * 计算并格式化doubles数值，保留两位有效数字
     *
     * @param doubles
     * @return 返回当前路程
     */
    private String formatDouble(Double doubles) {
        DecimalFormat format = new DecimalFormat("####.##");
        String distanceStr = format.format(doubles);
        return distanceStr.equals(getString(R.string.zero)) ? getString(R.string.double_zero)
                : distanceStr;
    }

    /**
     * 得到一个格式化的时间
     *
     * @param time
     * @return 时：分：秒：毫秒
     */
    private String getFormatTime(long time) {
        time = time / 1000;
        long second = time % 60;
        long minute = (time % 3600) / 60;
        long hour = time / 3600;
        // 毫秒秒显示两位
        // String strMillisecond = "" + (millisecond / 10);
        // 秒显示两位
        String strSecond = ("00" + second)
                .substring(("00" + second).length() - 2);
        // 分显示两位
        String strMinute = ("00" + minute)
                .substring(("00" + minute).length() - 2);
        // 时显示两位
        String strHour = ("00" + hour).substring(("00" + hour).length() - 2);

        return strHour + ":" + strMinute + ":" + strSecond;

    }

    /**
     * 设置当前步数和星标
     */
    private void changeStep() {
        int level = StepDetector.CURRENT_SETP / 100;//每走150步亮一颗星星
        switch (level) {
            case 10:
                iv_star_10.setImageResource(R.drawable.start_disable);
            case 9:
                iv_star_9.setImageResource(R.drawable.start_red);
            case 8:
                iv_star_8.setImageResource(R.drawable.start_red);
            case 7:
                iv_star_7.setImageResource(R.drawable.start_red);
            case 6:
                iv_star_6.setImageResource(R.drawable.start_red);
            case 5:
                iv_star_5.setImageResource(R.drawable.start_green);
            case 4:
                iv_star_4.setImageResource(R.drawable.start_green);
            case 3:
                iv_star_3.setImageResource(R.drawable.start_green);
            case 2:
                iv_star_2.setImageResource(R.drawable.start_green);
            case 1:
                iv_star_1.setImageResource(R.drawable.start_green);
                break;
            case 0:
                iv_star_1.setImageResource(R.drawable.star_enable);
                iv_star_2.setImageResource(R.drawable.star_enable);
                iv_star_3.setImageResource(R.drawable.star_enable);
                iv_star_4.setImageResource(R.drawable.star_enable);
                iv_star_5.setImageResource(R.drawable.star_enable);
                iv_star_6.setImageResource(R.drawable.star_enable);
                iv_star_7.setImageResource(R.drawable.star_enable);
                iv_star_8.setImageResource(R.drawable.star_enable);
                iv_star_9.setImageResource(R.drawable.star_enable);
                iv_star_10.setImageResource(R.drawable.star_enable);
                break;
        }
    }

    /**
     * 获取Activity相关控件
     */
    private void addView(View rootView) {
        tv_show_step = (TextView) rootView.findViewById(R.id.show_step);
        tv_week_day = (TextView) rootView.findViewById(R.id.week_day);
        tv_date = (TextView) rootView.findViewById(R.id.date);

        tv_timer = (TextView) rootView.findViewById(R.id.timer);

        tv_distance = (TextView) rootView.findViewById(R.id.distance);
        tv_calories = (TextView) rootView.findViewById(R.id.calories);
        tv_velocity = (TextView) rootView.findViewById(R.id.velocity);

        btn_start = (Button) rootView.findViewById(R.id.start);
        btn_stop = (Button) rootView.findViewById(R.id.stop);

        // 星标
        iv_star_1 = (ImageView) rootView.findViewById(R.id.iv_1);
        iv_star_2 = (ImageView) rootView.findViewById(R.id.iv_2);
        iv_star_3 = (ImageView) rootView.findViewById(R.id.iv_3);
        iv_star_4 = (ImageView) rootView.findViewById(R.id.iv_4);
        iv_star_5 = (ImageView) rootView.findViewById(R.id.iv_5);
        iv_star_6 = (ImageView) rootView.findViewById(R.id.iv_6);
        iv_star_7 = (ImageView) rootView.findViewById(R.id.iv_7);
        iv_star_8 = (ImageView) rootView.findViewById(R.id.iv_8);
        iv_star_9 = (ImageView) rootView.findViewById(R.id.iv_9);
        iv_star_10 = (ImageView) rootView.findViewById(R.id.iv_10);

        hide1 = (TableRow)rootView.findViewById(R.id.hide1);
        hide2 = (TableRow) rootView.findViewById(R.id.hide2);
        step_counter = (TextView) rootView.findViewById(R.id.step_counter);

        if (isRun) {
            hide1.setVisibility(View.GONE);
            hide2.setVisibility(View.GONE);
            step_counter.setText("您在步行");
        }}

    /**
     * 初始化界面
     */
    private void init() {
        //默认步长70，体重50
        step_length = SettingsActivity.sharedPreferences.getInt(
                SettingsActivity.STEP_LENGTH_VALUE, 70);
        weight = SettingsActivity.sharedPreferences.getInt(
                SettingsActivity.WEIGHT_VALUE, 50);

        countDistance();//计算距离
        countStep();//计算步数
        if ((timer += tempTime) != 0 && distance != 0.0) { //tempTime记录运动的总时间，timer记录每次运动时间

            // 体重、距离
            // 跑步热量（kcal）＝体重（kg）×距离（公里）×1.036，换算一下
            calories = weight * distance * 0.001;

            velocity = distance * 1000 / timer;
        } else {
            calories = 0.0;
            velocity = 0.0;
        }

        tv_timer.setText(getFormatTime(timer + tempTime));

        tv_distance.setText(formatDouble(distance));
        tv_calories.setText(formatDouble(calories));
        tv_velocity.setText(formatDouble(velocity));

        tv_show_step.setText(total_step + "");

        btn_start.setEnabled(!StepCounterService.FLAG);
        btn_stop.setEnabled(StepCounterService.FLAG);

        if (StepCounterService.FLAG) {
            btn_stop.setText(getString(R.string.pause));
        } else if (StepDetector.CURRENT_SETP > 0) {
            btn_stop.setEnabled(true);
            btn_stop.setText(getString(R.string.cancel));
        }

        setDate();
    }


    /**
     * 设置显示的日期
     */
    private void setDate() {
        Calendar mCalendar = Calendar.getInstance();// 获取日期实例
        int weekDay = mCalendar.get(Calendar.DAY_OF_WEEK);//周
        int month = mCalendar.get(Calendar.MONTH) + 1;// 月
        int day = mCalendar.get(Calendar.DAY_OF_MONTH);// 日

        tv_date.setText(month + getString(R.string.month) + day
                + getString(R.string.day));// 设置日期

        String week_day_str = new String();
        //设置星期
        switch (weekDay) {
            case Calendar.SUNDAY:
                week_day_str = getString(R.string.sunday);
                break;

            case Calendar.MONDAY:
                week_day_str = getString(R.string.monday);
                break;

            case Calendar.TUESDAY:
                week_day_str = getString(R.string.tuesday);
                break;

            case Calendar.WEDNESDAY:
                week_day_str = getString(R.string.wednesday);
                break;

            case Calendar.THURSDAY:
                week_day_str = getString(R.string.thursday);
                break;

            case Calendar.FRIDAY:
                week_day_str = getString(R.string.friday);
                break;

            case Calendar.SATURDAY:
                week_day_str = getString(R.string.saturday);
                break;
        }
        tv_week_day.setText(week_day_str);//设置星期
    }


    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onStart() {
        super.onStart();

        addView(rootView);
        if (SettingsActivity.sharedPreferences == null) {
            SettingsActivity.sharedPreferences = getActivity().getSharedPreferences(
                    SettingsActivity.SETP_SHARED_PREFERENCES,
                    Context.MODE_PRIVATE);//私有存储方式，其他应用无法访问
        }


        //开始按钮
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getActivity(), StepCounterService.class);
                gifView.showAnimation();
                getActivity().startService(service);
                btn_start.setEnabled(false);
                btn_stop.setEnabled(true);
                btn_stop.setText(getString(R.string.pause));
                startTimer = System.currentTimeMillis();
                tempTime = timer;
            }
        });

        //停止按钮
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getActivity(), StepCounterService.class);
                getActivity().stopService(service);
                gifView.showCover();
                if (StepCounterService.FLAG && StepDetector.CURRENT_SETP > 0) {
                    //如果服务在运行，并且步数大于0
                    btn_stop.setText(getString(R.string.cancel));
                } else {
                    //将界面数据初始化为0
                    StepDetector.CURRENT_SETP = 0;
                    tempTime = timer = 0;

                    btn_stop.setText(getString(R.string.pause));
                    btn_stop.setEnabled(false);

                    tv_timer.setText(getFormatTime(timer));//如果关闭之后，格式化时间

                    tv_show_step.setText("0");
                    tv_distance.setText(formatDouble(0.0));
                    tv_calories.setText(formatDouble(0.0));
                    tv_velocity.setText(formatDouble(0.0));

                    handler.removeCallbacks(thread);
                }
                btn_start.setEnabled(true);
            }
        });
    }

    @Override
    public void onDestroyView() {

        Log.i("__destroy___","1111111111111111111");
        super.onDestroyView();
    }
}