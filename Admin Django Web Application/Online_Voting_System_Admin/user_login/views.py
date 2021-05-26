from django.contrib import messages, auth
from django.contrib.auth import login,authenticate
from django.shortcuts import render, redirect
from django.http import HttpResponse, HttpResponseRedirect, JsonResponse
from django.contrib.auth.forms import AuthenticationForm
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User
from django.conf import settings
from .forms import voters_form

# Create your views here.
from .forms import NewUserForm



def register_request(request):

    if request.method== "POST":
        form = NewUserForm(request.POST)
        if form.is_valid():
            user=form.save()
            login(request,user)
            return redirect("dashboard")
    form=NewUserForm
    return render(request,'register.html',context={"register_form":form})
def login_request(request):
    if request.method=="POST":
        form =AuthenticationForm(request,data=request.POST)
        if form.is_valid():
            username=form.cleaned_data.get('username')
            password=form.cleaned_data.get('password')
            user=authenticate(username=username,password=password)
            if user is not None:
                login(request,user)
                messages.info(request,"Logged in")
                return redirect('dashboard')
            else:
                messages.error(request,"Invalid Username")
        else:
            messages.error(request,"Invalid Username or Password")
    form=AuthenticationForm()
    return render(request,'login.html',context={'login_form':form})


def urlredirector(request):
    return HttpResponseRedirect("login")
def login_ajax_request(request):

    if request.is_ajax():
       username=request.POST.get('name',None)
       password=request.POST.get('password',None)

       if username and password:

           user=authenticate(username=username,password=password)
           if user is not None:
               login(request,user)
               response={
                                  'msg':'Login Successful., Redirecting...,',
                                  'redirect':'yes'
                              }
           else:
               response={
                   'msg':'Invalid Username or Password',
                   'redirect':'no'
               }


           return JsonResponse(response)
def admin_secret_key(request):
    if request.is_ajax():
        key=request.POST.get('secret_key',None)
        print(key)
        if key is not None:
            if key==settings.ADMIN_SECRET_KEY:
                response= {
                                    'msg':'Key Valid',
                                    'register':'yes'
                                }

            else:
                 response = {
                                    'msg':'Invalid Key',
                                    'register':'no'
                                }

            return JsonResponse(response)
def add_voters(request):
    form=voters_form()
    return render(request,'add_voters.html',{'form':form})
def logout_user(request):
    if request.is_ajax():
        response= {}
        try:
            res=auth.logout(request)
            response['msg']='success'
            return JsonResponse(response)
        except:
            response['msg']='error'
            return JsonResponse(response)
