package kadai_007;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Posts_Chapter07 {

    public static void main(String[] args) {
    	Connection con = null;
        PreparedStatement pstmt = null;
        Statement statement = null;

        String[][] postList = {
            {"1003", "2023-02-08", "昨日の夜は徹夜でした・・", "13"},
            {"1002", "2023-02-08", "お疲れ様です！", "12"},
            {"1003", "2023-02-09", "今日も頑張ります！", "18"},
            {"1001", "2023-02-09", "無理は禁物ですよ！", "17"},
            {"1002", "2023-02-10", "明日から連休ですね！", "20"}
        };

        try {
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost/challenge_java",
                    "root",
                    "koto0618"
            );

            // テーブルが存在する場合、既存のデータを削除
            statement = con.createStatement();
            String clearTableSQL = "TRUNCATE TABLE posts";
            statement.executeUpdate(clearTableSQL);

            // テーブルを作成
            String createTableSQL = """
                    CREATE TABLE IF NOT EXISTS posts (
                    post_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
                    user_id INT(11) NOT NULL,
                    posted_at DATE NOT NULL,
                    post_content VARCHAR(255) NOT NULL,
                    likes INT(11) DEFAULT 0
                    );
                    """;
            statement.executeUpdate(createTableSQL);
            System.out.println("データベース接続成功:" + statement.toString() );

            // データの挿入
            String insertSQL = "INSERT INTO posts (user_id, posted_at, post_content, likes) VALUES (?, ?, ?, ?)";
            pstmt = con.prepareStatement(insertSQL);
            System.out.println("レコード追加を実行します");
            for (String[] post : postList) {
                pstmt.setInt(1, Integer.parseInt(post[0]));
                pstmt.setDate(2, java.sql.Date.valueOf(post[1]));
                pstmt.setString(3, post[2]);
                pstmt.setInt(4, Integer.parseInt(post[3]));
                pstmt.executeUpdate();
            }
            

            // ユーザーID 1002の投稿だけを取得
            String selectSQL = "SELECT * FROM posts WHERE user_id = 1002";
            ResultSet result = statement.executeQuery(selectSQL);
            System.out.println("ユーザーIDが1002のレコードを検索しました");
            while (result.next()) {
                int postId = result.getInt("post_id");
                int userId = result.getInt("user_id");
                String postedAt = result.getDate("posted_at").toString();
                String postContent = result.getString("post_content");
                int likes = result.getInt("likes");
                System.out.println(result.getRow() + "件目:投稿日時=" + postedAt + "/投稿の内容=" + postContent + "/いいね数=" + likes);
            }
        } catch (SQLException e) {
            System.out.println("エラー発生：" + e.getMessage());
        } finally {
            // 使用したオブジェクトを解放
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException ignore) {
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException ignore) {
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ignore) {
                }
            }
        }
    }
}
