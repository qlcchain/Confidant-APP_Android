package com.stratagile.pnrouter.gmail;

import com.google.api.services.gmail.Gmail;

import java.io.IOException;

// ...

public class MyClass {

    // ...


    /**
     * Immediately and permanently deletes the specified thread. This operation cannot
     * be undone. Prefer threads.trash instead.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param threadId ID of Thread to delete.
     * @throws IOException
     */
    public static void deleteThread(Gmail service, String userId, String threadId)
            throws IOException {
       /* service.users().threads().delete(userId, threadId).execute();*/
        System.out.println("Thread with id: " + threadId + " deleted successfully.");
    }

    // ...
}
