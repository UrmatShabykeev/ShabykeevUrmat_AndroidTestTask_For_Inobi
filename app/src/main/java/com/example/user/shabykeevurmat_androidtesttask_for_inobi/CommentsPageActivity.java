package com.example.user.shabykeevurmat_androidtesttask_for_inobi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class CommentsPageActivity extends AppCompatActivity {

    private String URL_Comments = "http://jsonplaceholder.typicode.com/comments?postId="; //ссылка на страницу с комментариями по id поста, далее будет решаться к какому id поста обращаться

    public ArrayList<CommentsModel> comments = new ArrayList<CommentsModel>(); //хранятся все комментарии
    ArrayAdapter<String> arrayAdapter; //адаптер для вывода в ListView комментариев к посту

    ArrayList<String> str_comments = new ArrayList<String>(); //

    private ListView commentsListView; //ListView с комментариями к посту

    String id_of_post = ""; //id выбранного поста, далее обьясню почему string

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_page);

        commentsListView = (ListView) findViewById(R.id.CommentsListView); //ListView с постами

        id_of_post = getIntent().getStringExtra("Id_of_Post"); //получить id textview, на которую нажал пользователь
        long tmp = Long.parseLong(id_of_post); //нужно увеличить его на 1 потому что id начинается с 0
        tmp++;
        id_of_post = String.valueOf(tmp); //переводим обратно в string

        URL_Comments = URL_Comments+id_of_post; //формируем ссылку на комментарии к нашему посту
        try
        {
            new GetComments().execute(URL_Comments); //начало метода чтения постов
        }
        catch (Exception ex) {}
    }

    //метод вызывается при нажатии кнопки "Добавить комментарий"
    public void AddCommentButton_pressed(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommentsPageActivity.this); //диалоговое окно для ввода данных

        //это ViewGroup для обьединения трех EditText
        //создается потому что без него несколько EditText перекрывают друг друга
        Context context = CommentsPageActivity.this;
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        //EditText для ввода Имени пользоваеля
        final EditText input_name = new EditText(CommentsPageActivity.this);
        LinearLayout.LayoutParams lp_name = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input_name.setLayoutParams(lp_name);
        input_name.setHint("Имя (обязательно)");
        input_name.setSingleLine(); //только одна строка
        layout.addView(input_name); //добавление в группу

        //EditText для ввода Email пользователя
        final EditText input_email = new EditText(CommentsPageActivity.this);
        LinearLayout.LayoutParams lp_email = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input_email.setLayoutParams(lp_email);
        input_email.setHint("Email (обязательно)");
        input_email.setSingleLine();
        layout.addView(input_email);

        //EditText для ввода комментария пользователя
        final EditText input_comment = new EditText(CommentsPageActivity.this);
        LinearLayout.LayoutParams lp_comment = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input_comment.setLayoutParams(lp_comment);
        input_comment.setHint("Комментарий (обязательно)");
        input_comment.setSingleLine();
        layout.addView(input_comment);

        builder.setTitle("Добавление комментария")
                .setCancelable(true)
                .setView(layout) //добавление в диалог группы EditText
                //кнопка "Добавить комментарий" и ее нажатие
                .setPositiveButton("Добавить", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialogInterface, int id){
                    //далее идет метод, вызываемый при нажатии на кнопку в диалоге
                    AddNewComment(input_name.getText().toString(), input_email.getText().toString(), input_comment.getText().toString());
                }});

        //вывод на экран пользователю
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    public void AddNewComment(String name, String email, String body)
    {
        //проверка на заполнение всех полей
        if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(body))
        {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //добавление комментария
            try
            {
                int new_size = str_comments.size()+1; //номер нового комментария на экране у пользователя
                String tmp = new_size + ")\n\n" + "PostId = " + id_of_post + "\n\nName = " + name + "\n\nBody = " + body;
                str_comments.add(tmp); //вывод на экран
                arrayAdapter.notifyDataSetChanged(); //вывод на экран
            }
            catch (Exception ex) {Toast.makeText(this, "Ошибка! Комментарий не добавлен!", Toast.LENGTH_SHORT).show();}
            Toast.makeText(this, "Ваш комментарий успешно добавлен!", Toast.LENGTH_SHORT).show();
        }
    }

    // класс для чтения комментариев с сервера. Все в целом такое же
    public class GetComments extends AsyncTask<String, Void, ArrayList<CommentsModel>> {    //класс для подключения к серверу в асинхронном режиме
        //изначально этот класс был в другом файле, но так как я никак не мог разобраться как возвращать данные из onPostExecute в Activity
        //в Интернете нашел способ, по которому этот класс включался в класс Activity

        public String result = null; //здесь храню результат чтения с сервера

        @Override
        protected ArrayList<CommentsModel> doInBackground(String... address)
        {
            try
            {
                URL url = new URL(address[0]);      //address[0] потому что ссылка только одна, а AsyncTask требует сразу несколько...
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();      //подключение URL
                conn.setConnectTimeout(15000 /* milliseconds */);       //лимит на время подключения
                conn.setReadTimeout(10000 /* milliseconds */);      //лимит на время чтения
                conn.setRequestMethod("GET");       //метод  - "получение данных с сервера"
                conn.setDoInput(true);
                conn.connect(); //подключиться

                int responsecode = conn.getResponseCode();      //должен быть 200
                if (responsecode != 200) throw new RuntimeException("HttpResponseCode: " + responsecode);
                else        //все ок
                {
                    Scanner sc = new Scanner(url.openStream());
                    while (sc.hasNext()) {
                        result += sc.nextLine();
                    }
                    sc.close();
                }
                conn.disconnect();      //закрытие подкючения

                //здесь я удаляю слово null из полученной строки
                String crappyPrefix = "null";
                if(result.startsWith(crappyPrefix))
                {
                    result = result.substring(crappyPrefix.length(), result.length());
                }
            }
            catch (Exception ex) {}   //try - catch выполнена именно так потому что весли сделать по нормальному, то возникает конфликст с "doInBackground AsyncTask"

            //парсирование
            try {
                JSONArray jArray = new JSONArray(result);
                for(int i=0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);

                    comments.add(new CommentsModel(jObject.getInt("postId"), jObject.getInt("id"), jObject.getString("name"), jObject.getString("email"), jObject.getString("body")));
                }
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }

            return comments;
        }

        @Override
        protected void onPostExecute(ArrayList<CommentsModel> a)
        {
            super.onPostExecute(a);
            int i=0;
            for(CommentsModel commentsModel : a)
            {
                int j=i+1;
                String tmp = j  + ")\n\n" + commentsModel.GetAll();
                str_comments.add(tmp);
                i++;
            }

            arrayAdapter = new ArrayAdapter<String>(CommentsPageActivity.this, R.layout.listviewcomments_item, str_comments);
            commentsListView.setAdapter(arrayAdapter);
        }
    }
}
