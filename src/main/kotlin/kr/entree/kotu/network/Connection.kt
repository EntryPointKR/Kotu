@file:Suppress("OverridingDeprecatedMember")

package kr.entree.kotu.network

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel

/**
 * Created by JunHyung Lim on 2020-03-27
 */
class Connection<T>(
    private val job: Job,
    private val channel: Channel<T>
) : Channel<T> by channel, Job by job {
    override fun cancel() {
        channel.cancel()
        job.cancel()
    }

    override fun cancel(cause: Throwable?): Boolean {
        channel.cancel()
        job.cancel()
        return true
    }

    override fun cancel(cause: CancellationException?) {
        channel.cancel(cause)
        job.cancel(cause)
    }
}