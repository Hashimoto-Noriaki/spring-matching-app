// DB とやり取りする窓口(3層でいう「倉庫係」)。User テーブル担当
package com.example.matching.repository;

import com.example.matching.entity.User;                      // 扱う対象のエンティティ
import org.springframework.data.jpa.repository.JpaRepository; // 継承する既製品の親
import java.util.Optional;                                    // 「見つからないかも」を表す入れ物

/**
 * users テーブル用の Repository。
 *
 * 【最重要ポイント】
 * これは class ではなく interface(メソッドの目次だけで中身が無い)。
 * 実装クラスは Spring Data JPA が起動時に自動生成してくれるので、
 * 私たちは「どんな検索が欲しいか」を宣言するだけ。SQL は1行も書かない。
 *
 * extends JpaRepository<User, Long>
 *   → <User, Long> は「User エンティティを、Long 型の id で扱う」という指定。
 *     この継承だけで save() / findById() / findAll() / delete() / count() など
 *     CRUD の基本メソッド一式が実装ゼロ行で使えるようになる。
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * メールアドレスでユーザーを1件検索する。
     *
     * メソッド名がそのままクエリになる(メソッド名クエリ):
     *   findByEmail → SELECT * FROM users WHERE email = ? が自動生成される。
     *   「Email」の部分は User エンティティのフィールド名 email と一致必須
     *   (typo すると実行時ではなく起動時にエラーで教えてくれる)。
     *
     * 戻り値の Optional<User> は「見つかるかもしれないし、いないかもしれない」を
     * 型で表現する入れ物。使う側は .orElseThrow() などで中身を取り出す。
     * null チェック忘れによるバグを型レベルで防ぐ仕組み。
     *
     * 【使い先】第5章 CustomUserDetailsService(ログイン時のユーザー取得)、
     *          第6章 UserService.findByEmail(ログイン中ユーザーの特定)
     */
    Optional<User> findByEmail(String email);

    /**
     * そのメールアドレスのユーザーが存在するかを true/false で返す。
     *
     * existsBy〜 も命名規則の一種(SELECT COUNT(*) > 0 相当が自動生成される)。
     * User 本体は要らず「いるかどうか」だけ知りたい場面用。
     *
     * 【使い先】第6章 UserService.register のメール重複チェック
     *   if (userRepository.existsByEmail(...)) { throw ... }
     */
    boolean existsByEmail(String email);
}
