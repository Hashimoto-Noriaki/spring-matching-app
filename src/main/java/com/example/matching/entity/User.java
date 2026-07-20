// ログイン情報のテーブル(認証に使う情報だけを持つ。表示用の情報は Profile が担当)
package com.example.matching.entity;   // このファイルの住所。フォルダ構成と一致している必要がある

// ---- import: このファイルで使う「よそのクラス」の取り寄せ宣言 ----
// エンティティ関連はすべて jakarta.persistence から取る(ここが鉄則)
import jakarta.persistence.CascadeType;    // cascade = CascadeType.ALL で使う
import jakarta.persistence.Column;         // @Column で使う
import jakarta.persistence.Entity;         // @Entity で使う
import jakarta.persistence.GeneratedValue; // @GeneratedValue で使う
import jakarta.persistence.GenerationType; // GenerationType.IDENTITY で使う
import jakarta.persistence.Id;             // @Id で使う
import jakarta.persistence.OneToOne;       // @OneToOne で使う
import jakarta.persistence.Table;          // @Table で使う

import java.time.LocalDateTime;            // 日時型

// ※ Profile は同じ entity パッケージにあるので import 不要!
// ※ org.springframework.context.annotation.Profile は名前が同じだけの別物。絶対に import しない

@Entity // 「このクラスは DB のテーブルになる」という宣言。起動時に Hibernate がテーブルを作る(ddl-auto=update の効果)
@Table(name = "users") // テーブル名を users に指定。指定しないと user になり、DB の予約語と衝突してエラーになる
public class User {

    @Id // 主キー(この行は何番、という通し番号)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 番号は DB が自動で振る(1, 2, 3...)。自分で採番しない
    private Long id; // private=クラス外から直接触れない / Long=null を入れられる整数型(保存前は番号未定=null のため)

    @Column(nullable = false, unique = true) // 「空っぽ禁止」「重複禁止」。同じメールで2人登録できないことを DB レベルで保証
    private String email; // ログインIDを兼ねるメールアドレス

    @Column(nullable = false) // 空っぽ禁止
    private String password; // ★平文は絶対に入れない。BCrypt でハッシュ化した文字列だけを入れる(第6章)

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 登録日時。new User() した瞬間の日時が初期値として入る

    /**
     * この User に紐づく Profile(1対1の関係)。
     *
     * mappedBy = "user"
     *   → 「この関係の管理者は Profile 側の user フィールド」という意味。
     *     外部キー(user_id 列)は profiles テーブル側に作られ、users 側には列を作らない。
     *     "user" という文字列は Profile.java 内のフィールド名を指す。
     *
     * cascade = CascadeType.ALL
     *   → User への操作(保存・削除など)を Profile にも連動させる。
     *     例: User を delete したら、その人の Profile も自動で delete される。
     *
     * orphanRemoval = true
     *   → 親(User)から切り離された Profile は孤児とみなして DB からも消す。
     *     退会処理などで Profile だけがゴミとして残るのを防ぐ保険。
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Profile profile; // ← import 無しでこう書けるのが正解(同一パッケージだから)

    // ---- getter / setter ----
    // フィールドが private なので、外部はここを経由してのみ値に触れる

    public Long getId() { return id; }                     // id は読み取り専用(DB が採番するので setter を作らない)
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public LocalDateTime getCreatedAt() { return createdAt; } // 登録日時も読み取り専用(後から書き換えさせない)
    public Profile getProfile() { return profile; }

    public void setEmail(String email) { this.email = email; }           // 第6章の UserService が登録時に呼ぶ
    public void setPassword(String password) { this.password = password; } // 同上(ハッシュ化済みの値を渡す)
    public void setProfile(Profile profile) { this.profile = profile; }
    // this.email = email … 「引数の email を、このオブジェクトのフィールド email に入れる」。
    // 引数とフィールドが同名なので this. で区別している
}