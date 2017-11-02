package akkauth.core.tokens.algebras

import akkauth.AccountId

trait TokenRepository[RefreshToken, M[_]] {
  def storeRefreshToken(accountId: AccountId, refreshToken: RefreshToken): M[Unit]
  def invalidateRefreshToken(accountId: AccountId, refreshToken: RefreshToken): M[Boolean]
}
