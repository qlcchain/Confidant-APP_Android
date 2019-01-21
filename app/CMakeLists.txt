# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

add_library( # Sets the name of the library.
        tox

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        src/main/cpp/toxcore/assoc.h
        src/main/cpp/toxcore/assoc.c
        src/main/cpp/toxcore/ccompat.h
        src/main/cpp/toxcore/crypto_core.c
        src/main/cpp/toxcore/crypto_core.h
        src/main/cpp/toxcore/DHT.c
        src/main/cpp/toxcore/DHT.h
        src/main/cpp/toxcore/friend_connection.c
        src/main/cpp/toxcore/friend_connection.h
        src/main/cpp/toxcore/friend_requests.c
        src/main/cpp/toxcore/friend_requests.h
        src/main/cpp/toxcore/group.c
        src/main/cpp/toxcore/group.h
        src/main/cpp/toxcore/LAN_discovery.c
        src/main/cpp/toxcore/LAN_discovery.h
        src/main/cpp/toxcore/list.c
        src/main/cpp/toxcore/list.h
        src/main/cpp/toxcore/logger.c
        src/main/cpp/toxcore/logger.h
        src/main/cpp/toxcore/Makefile.inc
        src/main/cpp/toxcore/Messenger.c
        src/main/cpp/toxcore/Messenger.h
        src/main/cpp/toxcore/misc_tools.h
        src/main/cpp/toxcore/net_crypto.c
        src/main/cpp/toxcore/net_crypto.h
        src/main/cpp/toxcore/network.c
        src/main/cpp/toxcore/network.h
        src/main/cpp/toxcore/onion.c
        src/main/cpp/toxcore/onion.h
        src/main/cpp/toxcore/onion_announce.c
        src/main/cpp/toxcore/onion_announce.h
        src/main/cpp/toxcore/onion_client.c
        src/main/cpp/toxcore/onion_client.h
        src/main/cpp/toxcore/ping.c
        src/main/cpp/toxcore/ping.h
        src/main/cpp/toxcore/ping_array.c
        src/main/cpp/toxcore/ping_array.h
        src/main/cpp/toxcore/TCP_client.c
        src/main/cpp/toxcore/TCP_client.h
        src/main/cpp/toxcore/TCP_connection.c
        src/main/cpp/toxcore/TCP_connection.h
        src/main/cpp/toxcore/TCP_server.c
        src/main/cpp/toxcore/TCP_server.h
        src/main/cpp/toxcore/tox.c
        src/main/cpp/toxcore/tox.h
        src/main/cpp/toxcore/tox_old.h
        src/main/cpp/toxcore/tox_old_code.h
        src/main/cpp/toxcore/util.c
        src/main/cpp/toxcore/util.h
        src/main/cpp/toxcore/ppTox.c
        src/main/cpp/toxcore/ppTox.h
        )

add_library( sodium
        SHARED
        IMPORTED )
set_target_properties( sodium
        PROPERTIES IMPORTED_LOCATION
        ../../../../libs/${ANDROID_ABI}/libsodium.so )

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../../libs)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
        tox
        sodium
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})
include_directories(libs/include)