package com.example.user.shabykeevurmat_androidtesttask_for_inobi;

/**
 * Created by User on 16.01.2019.
 */

public class CommentsModel {
    public int postId, id;
    public String name, email, body;

    public CommentsModel(int postId, int id, String name, String email, String body)
    {
        this.postId = postId;
        this.id = id;
        this.name = name;
        this.email = email;
        this.body = body;
    }

    public int GetPostId() {return postId;}
    public int GetId() {return id;}
    public String GetName() {return name;}
    public String GetEmail() {return email;}
    public String GetBody() {return body;}

    public String GetNameBody()
    {
        String result = "", temp1 = "", temp2 = "";

        temp1 = "Name = " + GetName();
        temp2 = "Body = " + GetBody();

        result = temp1 + "\n\n" + temp2;
        return  result;
    }

    public String GetAll()
    {
        String result = "", temp1 = "", temp2 = "", temp3 = "", temp4 = "", temp5 = "";

        temp1 = "PostId = " + String.valueOf(GetPostId());
        temp2 = "Id = " + String.valueOf(GetId());
        temp3 = "Name = " + GetName();
        temp4 = "Email = " + GetEmail();
        temp5 = "Body = " + GetBody();

        result = temp1 + "\n\n" + temp2 + "\n\n" + temp3 + "\n\n" +  temp4 + "\n\n" +  temp5;
        return  result;
    }
}
