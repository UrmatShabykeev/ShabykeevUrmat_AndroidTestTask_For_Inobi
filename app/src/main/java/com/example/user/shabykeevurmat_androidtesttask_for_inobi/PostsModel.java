package com.example.user.shabykeevurmat_androidtesttask_for_inobi;

//class for model of posts

public class PostsModel {
    public int UserId, id;
    public String title, body;

    public PostsModel(int UserId, int id, String title, String body)
    {
        this.UserId = UserId;
        this.id = id;
        this.title = title;
        this.body = body;
    }

    public int GetUserId() {return UserId;}
    public int GetId() {return id;}
    public String GetTitle() {return title;}
    public String GetBody() {return body;}

    public String GetTitleAndBody()
    {
        String result = "", temp1 = "", temp2 = "";

        temp1 = "Title = " + GetTitle();
        temp2 = "Body = " + GetBody();

        result = temp1 + "\n\n" + temp2;
        return  result;
    }

    public String GetAll()
    {
        String result = "", temp1 = "", temp2 = "", temp3 = "", temp4 = "";

        temp1 = "UserId = " + String.valueOf(GetUserId());
        temp2 = "id = " + String.valueOf(GetId());
        temp3 = "Title = " + GetTitle();
        temp4 = "Body = " + GetBody();

        result = temp1 + "\n\n" + temp2 + "\n\n" + temp3 + "\n\n" + temp4;
        return  result;
    }
}
