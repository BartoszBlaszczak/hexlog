function resize_posts() {
    Array.from(document.getElementsByClassName('post-frame')).forEach(resize_post)
}

function resize_post(post_frame) {
    post_frame.style.height=post_frame.contentWindow.document.body.offsetHeight+60+"px"
    if (post_frame.parentElement.previousElementSibling.classList.contains('active'))
        post_frame.parentElement.style.maxHeight = post_frame.parentElement.scrollHeight + "px"
}

function toggle_collapse(post_header) {
    post_header.classList.toggle("active");
    let post_panel = post_header.nextElementSibling;
    toggle_height_of(post_panel)
}

function toggle_height_of(element) {
    element.style.maxHeight = element.style.maxHeight ? null : element.scrollHeight + "px"
}

function open_post() {
    let anchor = window.location.hash.slice(1);
    let post_header = anchor ? document.getElementById(anchor) : document.getElementsByClassName("post-header")[0]
    post_header?.click()
}

document.addEventListener("DOMContentLoaded", open_post);