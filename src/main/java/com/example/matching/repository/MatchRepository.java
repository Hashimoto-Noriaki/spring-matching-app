// DB とやり取りする窓口(倉庫係)。matches テーブル担当
package com.example.matching.repository;

import com.example.matching.entity.Match;                     // 扱う対象のエンティティ
import org.springframework.data.jpa.repository.JpaRepository; // 継承する既製品の親
import java.util.List;                                        // 複数件の検索結果を受け取る型

/**
 * matches テーブル用の Repository。
 * 「自分が絡むマッチを全部くれ」という一覧取得だけを担当する。
 */
public interface MatchRepository extends JpaRepository<Match, Long> {

    /**
     * 「user1 が自分」または「user2 が自分」のマッチを全件返す。
     *
     * 命名の分解:
     *   findBy + User1Id + Or + User2Id
     *     ↑        ↑       ↑      ↑
     *   検索    user1の   OR   user2の
     *          中の id   条件   中の id
     *
     * 自動生成される SQL:
     *   SELECT * FROM matches
     *    WHERE user1_id = ? OR user2_id = ?
     *
     * 【なぜ Or が必要か】
     * Match は「小さい id を user1 に入れる」ルール(第4章 4-6)なので、
     * 自分が user1 側に入っているか user2 側に入っているかは相手次第。
     *   例: 自分が id=5 の場合
     *     id=3 とのマッチ → 自分は user2 側
     *     id=8 とのマッチ → 自分は user1 側
     * どちら側にいても拾えるように、両方の列を Or で見る必要がある。
     *
     * 【引数が2つある理由(ちょっと不格好な点)】
     * 実際の呼び出しでは両方に同じ自分の id を渡す:
     *   matchRepository.findByUser1IdOrUser2Id(me.getId(), me.getId());
     * 「同じ値を2回渡す」のは冗長に見えるが、メソッド名クエリの仕組み上、
     * 条件(User1Id と User2Id)1つにつき引数が1つ必要なため。
     * ※ これを1引数にしたければ @Query で JPQL を自書きする方法もある(発展)。
     *
     * 戻り値は List(0件なら空リスト。マッチがまだ無い人でも安全)。
     *
     * 【使い先】第9章 LikeService.findMatchesOf → マッチ一覧画面。
     * 取れた各 Match は、画面側で m.getPartnerOf(me) を呼んで「相手」に変換する
     * (Repository が「自分の絡む行」を集め、エンティティが「相手はどっちか」を判定、
     *  という役割分担になっている)。
     */
    List<Match> findByUser1IdOrUser2Id(Long user1Id, Long user2Id);
}
