package com.stratagile.pnrouter.data.web

/**
 * A class representing a message destination or origin.
 */
class SignalServiceAddress
/**
 * Construct a PushAddress.
 *
 * @param e164number The Signal Service username of this destination (eg e164 representation of a phone number).
 * @param relay The Signal SErvicefederated server this user is registered with (if not your own server).
 */
@JvmOverloads constructor(val number: String?, val relay: Optional<String> = Optional.absent()) {

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is SignalServiceAddress) return false

        val that = other as SignalServiceAddress?

        return equals(this.number, that!!.number) && equals(this.relay, that.relay)
    }

    override fun hashCode(): Int {
        var hashCode = 0

        if (this.number != null) hashCode = hashCode xor this.number.hashCode()
        if (this.relay.isPresent) hashCode = hashCode xor this.relay.get().hashCode()

        return hashCode
    }

    private fun equals(one: String?, two: String?): Boolean {
        return if (one == null) two == null else one == two
    }

    private fun equals(one: Optional<String>, two: Optional<String>): Boolean {
        return if (one.isPresent)
            two.isPresent && one.get().equals(two.get())
        else
            !two.isPresent
    }

    companion object {

        val DEFAULT_DEVICE_ID = 1
    }
}
