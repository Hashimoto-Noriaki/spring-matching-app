// DB とやり取りする窓口(倉庫係)。profiles テーブル担当
package com.example.matching.repository;

import com.example.matching.entity.Profile;                   // 扱う対象のエンティティ
import java.util.List;                                        // 複数件の検索結果を受け取る型
import java.util.Optional;                                    // 「見つからないかも」を表す入れ物
import org.springframework.data.jpa.repository.JpaRepository; // 継承する既製品の親

/**
 * profiles テーブル用の Repository。
 *
 * extends JpaRepository<Profile, Long>
 *   → 「Profile エンティティを、Long 型の id で扱う」という指定。
 *     UserRepository と同じく、save() / findById() / findAll() / delete() などの
 *     基本 CRUD はこの継承だけで使える(実装は Spring Data JPA が自動生成)。
 */
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    /**
     * user_id でプロフィールを1件検索する。
     *
     * 命名の仕掛け: Profile に userId というフィールドは無い。あるのは user(User型)。
     * Spring Data JPA は findByUserId を「user フィールドの中の id」とネストを辿って解釈し、
     *   SELECT * FROM profiles WHERE user_id = ?
     * を自動生成する(findBy + User + Id と分解して読む)。
     *
     * 戻り値が Optional なのは、その人がまだプロフィール未作成かもしれないから。
     *
     * 【使い先】第7章 ProfileService の取得・更新・削除(全部「まず本人の Profile を引く」から始まる)
     */
    Optional<Profile> findByUserId(Long userId);

    /**
     * そのユーザーのプロフィールが存在するかを true/false で返す。
     * Profile 本体は要らず「もう作ってあるか」だけ知りたい場面用。
     *
     * 【使い先】第7章 ProfileService.create の二重作成チェック
     *   if (profileRepository.existsByUserId(...)) { throw ... }
     */
    boolean existsByUserId(Long userId);

    /**
     * 指定ユーザー「以外」の全プロフィールを返す。
     *
     * Not を付けると条件が反転する:
     *   findByUserIdNot → SELECT * FROM profiles WHERE user_id <> ?
     *
     * 戻り値が List なのは複数件返るから。該当0件でも null ではなく
     * 「空のリスト」が返るので、List に Optional は不要(重要な感覚)。
     *
     * 【使い先】第8章 DiscoverController(「さがす」画面で自分を除いた一覧を出す)
     */
    List<Profile> findByUserIdNot(Long userId);
}