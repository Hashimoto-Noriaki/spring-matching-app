// DB とやり取りする窓口(倉庫係)。likes テーブル担当
package com.example.matching.repository;

import com.example.matching.entity.Like;                      // 扱う対象のエンティティ
import org.springframework.data.jpa.repository.JpaRepository; // 継承する既製品の親

/**
 * likes テーブル用の Repository。
 *
 * メソッドが1つしか無いのに、このアプリで一番重要な Repository。
 * 「マッチが成立するか」の判定は、ここの exists 1本で決まる。
 */
public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * 「fromId さんが toId さんに、既にいいねを送っているか」を true/false で返す。
     *
     * 命名の分解(この章で一番複雑なメソッド名):
     *   existsBy + FromUserId + And + ToUserId
     *      ↑          ↑          ↑       ↑
     *   有無だけ   fromUser     AND    toUser
     *   返す      の中の id    条件連結  の中の id
     *
     * つまりネスト解釈(fromUser.id / toUser.id)を2回やりつつ、And で繋いでいる。
     * 自動生成される SQL:
     *   SELECT COUNT(*) > 0 FROM likes
     *    WHERE from_user_id = ? AND to_user_id = ?
     *
     * 引数の順番はメソッド名の順に対応する:
     *   第1引数 fromId → FromUserId に、第2引数 toId → ToUserId に入る。
     *   ※ 逆に渡すと「片思いの向き」が逆転するバグになるのに、型は同じ Long 同士
     *     なのでコンパイルエラーにならない。呼ぶ側が気をつける箇所(第9章で注意)。
     *
     * 【使い先】第9章 LikeService.sendLike で2役をこなす:
     *   ① alreadyLiked(二重いいね防止)… exists(自分→相手)
     *   ② マッチ判定 … いいね保存後に exists(相手→自分)を調べ、
     *      true なら相互いいね=マッチ成立。このアプリの心臓部。
     */
    boolean existsByFromUserIdAndToUserId(Long fromId, Long toId);
}
