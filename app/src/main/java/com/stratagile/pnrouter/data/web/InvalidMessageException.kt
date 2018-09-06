package com.stratagile.pnrouter.data.web

class InvalidMessageException : Exception {

    constructor() {}

    constructor(detailMessage: String) : super(detailMessage) {}

    constructor(throwable: Throwable) : super(throwable) {}

    constructor(detailMessage: String, throwable: Throwable) : super(detailMessage, throwable) {}

    constructor(detailMessage: String, exceptions: List<Exception>) : super(detailMessage, exceptions[0]) {}
}
