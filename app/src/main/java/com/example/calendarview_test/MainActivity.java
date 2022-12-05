package com.example.calendarview_test;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateLongClickListener;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;


public class MainActivity extends AppCompatActivity implements OnDateSelectedListener {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEE, d MMM yyyy");
    public String readDay = null;  //선택한 일자 읽어오는 변수
    public String str = null; //EditText에 입력되는 내용을 저장하는 변수
    public CalendarView calendarView; //캘린더뷰 변수
    public Button changeButton, deleteButton, saveButton; //수정, 삭제, 저장 버튼
    public TextView diaryTextView, contentTextView; //diaryTextView = 선택한 날짜를 표시해줌, contentTextView = 일정 내용(str변수의 값)을 저장하는 변수
    public EditText contextEditText; //입력 창 관련 변수

    //데코 삭제를 위한 테스트 변수
    //private EventDecorator eventDecorator;

    @BindView(R.id.calendarView)
    MaterialCalendarView widget;

    @BindView(R.id.diaryTextView)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        widget.setOnDateChangedListener(this);
        //widget.setOnDateLongClickListener(this);
        //widget.setOnMonthChangedListener(this);

        //Setup initial text
        textView.setText("날짜를 선택해주세요.");

        //MaterialCalendarView calendarView = findViewById(R.id.calendarView);
        //calendarView.setSelectedDate(CalendarDay.today());

        //calendarView.addDecorators(new SaturdayDecorator(), new SundayDecorator());
        widget.setSelectedDate(CalendarDay.today());
        widget.addDecorators(new SaturdayDecorator(), new SundayDecorator());

        //오늘 날짜에 도트찍는 코드
        //calendarView.addDecorator(new EventDecorator(Color.RED, Collections.singleton(CalendarDay.today())));

        //캘린더뷰 관련 변수들

        diaryTextView = findViewById(R.id.diaryTextView);
        saveButton = findViewById(R.id.saveButton);
        deleteButton = findViewById(R.id.deleteButton);
        changeButton = findViewById(R.id.changeButton);
        contentTextView = findViewById(R.id.contentTextView);
        //textView3 = findViewById(R.id.textView3);
        contextEditText = findViewById(R.id.contextEditText);

    }

    @Override
    public void onDateSelected(
            @NonNull MaterialCalendarView widget,
            @NonNull CalendarDay date,
            boolean selected) {
        //textView.setText(selected ? FORMATTER.format(date.getDate()) : "날짜를 선택해주세요.");
        //diaryTextView.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        contextEditText.setVisibility(View.VISIBLE);
        contentTextView.setVisibility(View.INVISIBLE);
        changeButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
        //diaryTextView.setText(String.format("%d / %d / %d", date.getYear(), date.getMonth(), date.getDay()));
        contextEditText.setText("");
        checkDay(date.getYear(), date.getMonth(), date.getDay());

        saveButton.setOnClickListener(new View.OnClickListener() //저장 버튼을 누르면 입력한 내용을 해당 일자에 저장하는 메소드 / 저장 버튼 클릭 이벤트 처리
        {
            @Override
            public void onClick(View view)
            {
                saveDiary(readDay);
                str = contextEditText.getText().toString();
                contentTextView.setText(str);
                saveButton.setVisibility(View.INVISIBLE);
                changeButton.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                contextEditText.setVisibility(View.INVISIBLE);
                contentTextView.setVisibility(View.VISIBLE);
                //eventDecorator = new EventDecorator(Color.RED, Collections.singleton(date));
                //widget.addDecorator(eventDecorator);
                widget.addDecorator(new EventDecorator(Color.RED, Collections.singleton(date)));
                widget.invalidateDecorators();

            }
        });

        changeButton.setOnClickListener(new View.OnClickListener()  //수정 버튼을 누르면 입력한 내용을 불러와 수정할 수 있게 해주는 메소드 / 수정 버튼 클릭 이벤트 처리
        {
            @Override
            public void onClick(View view)
            {
                contextEditText.setVisibility(View.VISIBLE);
                contentTextView.setVisibility(View.INVISIBLE);
                contextEditText.setText(str);

                saveButton.setVisibility(View.VISIBLE);
                changeButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
                contentTextView.setText(contextEditText.getText());
            }

        });

        deleteButton.setOnClickListener(new View.OnClickListener() //삭제 버튼을 누르면 해당 일자에 저장된 일정을 삭제해주는 메소드 / 삭제 버튼 클릭 이벤트 처리
        {
            @Override
            public void onClick(View view)
            {
                contentTextView.setVisibility(View.INVISIBLE);
                contextEditText.setText("");
                contextEditText.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);
                changeButton.setVisibility(View.INVISIBLE);
                deleteButton.setVisibility(View.INVISIBLE);
                removeDiary(readDay);
                //widget.removeDecorator(eventDecorator);
            }
        });

        if (contentTextView.getText().length() == 0)
        {
            contentTextView.setVisibility(View.INVISIBLE);
            diaryTextView.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            changeButton.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
            contextEditText.setVisibility(View.VISIBLE);
        }
    }


    public void checkDay(int cYear, int cMonth, int cDay) //선택한 일자를 readDay 변수에 저장하는 메소드
    {
        readDay = "" + cYear + "-" + (cMonth + 1) + "" + "-" + cDay + ".txt";
        FileInputStream fis;

        try
        {
            fis = openFileInput(readDay);

            byte[] fileData = new byte[fis.available()];
            fis.read(fileData);
            fis.close();

            str = new String(fileData);

            contextEditText.setVisibility(View.INVISIBLE);
            contentTextView.setVisibility(View.VISIBLE);
            contentTextView.setText(str);

            saveButton.setVisibility(View.INVISIBLE);
            changeButton.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @SuppressLint("WrongConstant")
    public void removeDiary(String readDay) //선택한 일자에 저장한 내용을 삭제하는 메소드
    {
        FileOutputStream fos;
        try
        {
            fos = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS);
            String content = "";
            fos.write((content).getBytes());
            fos.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public void saveDiary(String readDay) //선택한 일자에 입력 내용을 저장하는 메소드
    {
        FileOutputStream fos;
        try
        {
            fos = openFileOutput(readDay, MODE_NO_LOCALIZED_COLLATORS);
            String content = contextEditText.getText().toString();
            fos.write((content).getBytes());
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    //캘린더에 도트 찍어주는 클래스
    class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }

    class SaturdayDecorator implements DayViewDecorator {

        //private final Calendar calendar = Calendar.getInstance();

        public SaturdayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {;
            //day.copyTo(calendar);
            int weekDay = day.getDate().with(DayOfWeek.SATURDAY).getDayOfMonth();
            return weekDay == day.getDay();
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.BLUE));
        }
    }

    class SundayDecorator implements DayViewDecorator {

        //private final Calendar calendar = Calendar.getInstance();

        public SundayDecorator() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            int weekDay = day.getDate().with(DayOfWeek.SUNDAY).getDayOfMonth();
            return weekDay == day.getDay();
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.RED));
        }
    }

    /*
    @Override
    public void onDateLongClick(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date) {
        final String text = String.format("%s is available", FORMATTER.format(date.getDate()));
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        widget.addDecorator(new EventDecorator(Color.RED, Collections.singleton(date)));
    }*/

    /*
    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        //noinspection ConstantConditions
        getSupportActionBar().setTitle(FORMATTER.format(date.getDate()));
    }
     */

}





