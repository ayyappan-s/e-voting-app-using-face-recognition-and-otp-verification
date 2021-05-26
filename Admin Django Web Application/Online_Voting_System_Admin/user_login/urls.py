from django.urls import path
from .views import *

urlpatterns = [
    path("register",register_request,name="register"),
    path("login",login_request,name="login"),
    path("",urlredirector,name='urlredirector'),
    path("login_ajax_request",login_ajax_request,name="login_ajax_request"),
    path("admin_secret_key",admin_secret_key,name="admin_secret_key"),
    path("add_voters",add_voters,name="add_voters"),
    path("logout",logout_user,name='logout')
]