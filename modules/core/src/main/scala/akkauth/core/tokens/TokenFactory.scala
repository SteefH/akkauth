package akkauth.core.tokens

import akkauth.AccountId

trait TokenFactory[T, M[_]] {

  def createToken(accountId: AccountId): M[T]
}
