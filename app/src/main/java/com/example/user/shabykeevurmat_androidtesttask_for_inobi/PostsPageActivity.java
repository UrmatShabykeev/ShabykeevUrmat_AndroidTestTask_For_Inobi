package com.example.user.shabykeevurmat_androidtesttask_for_inobi;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class PostsPageActivity extends AppCompatActivity {

    private final static String TAG = "PostsPageActivity";
    private final static String URL_Posts = "http://jsonplaceholder.typicode.com/posts";
    //private final static int maxAmountOfPosts = 30;  //по условию задачи требуется отображать только 30 постов

    private ListView PostsListView; //view для постов

    public ArrayList<PostsModel> posts = new ArrayList<PostsModel>(); //хранятся все посты

    ArrayList<String> str_posts = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_page);

        try
        {
            new GetPosts().execute(URL_Posts); //начало метода чтения постов
        }
        catch (Exception ex) {}

        PostsListView = (ListView) findViewById(R.id.PostListView); //ListView с постами

        //обработчик нажатия на TextBox, который хранит в себе пост
        PostsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                //переход на второй экран (комментарии к посту)
                startActivity(new Intent(PostsPageActivity.this, CommentsPageActivity.class).putExtra("Id_of_Post", String.valueOf(id)));
            }
        });
    }

    public class GetPosts extends AsyncTask<String, Void, ArrayList<PostsModel>> {    //класс для подключения к серверу в асинхронном режиме
        //изначально этот класс был в другом файле, но так как я никак не мог разобраться как возвращать данные из onPostExecute в Activity
        //в Интернете нашел способ, по которому этот класс включался в класс Activity
        private static final String TAG = "GetPostsClass"; // нужно для Log

        public String result = null; //здесь храню результат чтения с сервера

        @Override
        protected ArrayList<PostsModel> doInBackground(String... address)
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

                    posts.add(new PostsModel(jObject.getInt("userId"), jObject.getInt("id"), jObject.getString("title"), jObject.getString("body")));
                }
            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }

            return posts;
        }

        @Override
        protected void onPostExecute(ArrayList<PostsModel> a)
        {
            super.onPostExecute(a);

            int i=0;
            for(PostsModel postsModel : a)
            {

               // if(i<maxAmountOfPosts)
               // {
                   int j=i+1;
                    String tmp = j  + ")\n\n" + postsModel.GetAll();
                    str_posts.add(tmp);
                //}
                i++;
            }

            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(PostsPageActivity.this, R.layout.listview_item, str_posts);
            PostsListView.setAdapter(arrayAdapter);
        }
    }
}
