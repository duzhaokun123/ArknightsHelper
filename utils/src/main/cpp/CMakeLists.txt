cmake_minimum_required(VERSION 3.18.1)

project("utils")

add_library(
        utils
        SHARED
        utils.cpp)

find_library(
        log-lib
        log)

set(OpenCV_DIR "${CMAKE_CURRENT_SOURCE_DIR}/../../../../third_party/OpenCV-android-sdk/sdk/native/jni")
message(STATUS "opencv dir: ${OpenCV_DIR}")
find_package(OpenCV REQUIRED)

target_link_libraries(
        utils
        ${log-lib}
        ${OpenCV_LIBS}
        jnigraphics)