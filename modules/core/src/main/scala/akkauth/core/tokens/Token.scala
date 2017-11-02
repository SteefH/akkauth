package akkauth.core.tokens

import simulacrum.typeclass

import akkauth.AccountId

/**
  * Token Typeclass
  * Tokens are used as a means of authentication and can be valid or invalid (eg. expired, malformed. etc.). Valid
  * tokens are expected to contain an AccountId, which can be extracted from the token by calling this typeclass'
  * `accountId` method. Tokens also have a lifetime, which invalidates the token when this lifetime has expired
  * @tparam T
  */
@typeclass trait Token[T] {

  /**
    * Extract the account ID from a token.
    *
    * @param token The token to get the account id from
    * @return `Some(accountId)` if the token is valid and contains a certain account ID, `None` otherwise
    */
  def accountId(token: T): Option[AccountId]
  def isValid(token: T): Boolean
  def expiresAt(token: T): Option[Long]

  def fromString(tokenString: String): T
  def asString(token: T): String
}
