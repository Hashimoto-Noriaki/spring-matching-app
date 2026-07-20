package com.example.matching.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(name = "matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user1_id")
    private User user1; // ★必ず「id が小さい方」を入れるルールにする

    @ManyToOne(optional = false)
    @JoinColumn(name = "user2_id")
    private User user2;

    @Column(nullable = false)
    private LocalDateTime matchedAt = LocalDateTime.now();

    // ---- getter / setter ----
    public Long getId() { return id; }
    public User getUser1() { return user1; }
    public void setUser1(User user1) { this.user1 = user1;}
    public User getUser2() { return user2;}
    public void setUser2(User user2) { this.user2 = user2; }
    public LocalDateTime getMatchedAt() { return matchedAt; }

    /**
     * 自分(me)を渡すと「マッチの相手」を返す便利メソッド。
     *
     * 【なぜ必要か】
     * matches テーブルは (user1, user2) のペアで1行を保存するが、
     * 「どちらが相手か」は見る人によって変わる。
     *   例: user1=Alice, user2=Bob の行の場合
     *       Alice から見た相手 → Bob (user2)
     *       Bob   から見た相手 → Alice (user1)
     * その判定をこのメソッドに閉じ込めることで、
     * マッチ一覧画面(第9章)は m.getPartnerOf(me) と呼ぶだけで済む。
     *
     * 【構文】
     * public User getPartnerOf(User me)
     *   ↑      ↑       ↑         ↑
     *  公開   戻り値   メソッド名  引数(User型を受け取り me と名付ける)
     *
     * @param me ログイン中の自分(User エンティティ)
     * @return 自分ではない方のユーザー(=マッチの相手)
     */
    public User getPartnerOf(User me) {
        // user1 の id と自分の id を比較して、自分がどちら側かを判定する。
        //
        // ・比較は == ではなく .equals() を使う。
        //   Long はオブジェクトなので、== だと「同じ実体か」の比較になり、
        //   値が同じでも false になることがある(Javaの有名な罠)。
        //   ラッパー型(Long/Integer)の値比較は equals、が鉄則。
        //
        // ・「条件 ? A : B」は三項演算子(条件が true なら A、false なら B)。
        //   if文で書くと:
        //     if (user1.getId().equals(me.getId())) {
        //         return user2;   // 自分が user1 側 → 相手は user2
        //     } else {
        //         return user1;   // 自分が user2 側 → 相手は user1
        //     }
        return user1.getId().equals(me.getId()) ? user2 : user1;
    }
}
