% Main Function
function ImageSharing = ImageSharing(path)
     close all;

    t = 4;
    w = 8;
    Users = [1,3,5,8];
    base = 257;



    if(length(Users)<t)
        error('invalid length of Users');
    end;


    tSetup = zeros(1,10);
    tExec = zeros(1,10);

    Im_Name = path;
    for i=1:10
        tic;
        [Im_linear, Im_Square, Height,Width] = ReadImage(Im_Name);

        Sub_width  = Width/t^(0.5);
        Sub_height = Height/t^(0.5);
        if(mod(Sub_height,1) ~= 0)
            error('invalid length of Sub height');
        end;

        P_Im = GetPreparedIm(Im_linear,t);
        tSetup(i)=toc;

        tic;
        Shadow_Image = GetShadow_Image(P_Im,w,base);

        %a = find(Shadow_Image>256);
        %ShowShadowImage(Shadow_Image,Sub_width,Sub_height);

        RequiredShadowImage = GetRequiredShadowImage(Shadow_Image,Users);



        % RequiredShadowImage = AddNoise(RequiredShadowImage);

        RecoveredImage = RecoverImage(RequiredShadowImage,Users,Height,Width,base);
        RecoveredImage = RecoveredImage';
        Err = sum(uint8(RecoveredImage(:)) - Im_linear);
        %disp(sprintf('Error bit: %d',Err));
        %figure;
        %imshow(uint8(RecoveredImage));
        %title('Recovered Image');
        tExec(i) = toc;
    end
    tSetup;
    tExec;
    disp('execution time');
    disp('Standard Deviation execution time');
    std(tExec)
    disp('Average execution time');
    mean(tExec)
    disp('Minimum execution time');
    min(tExec)
    disp('Maximum execution time');
    max(tExec)

    disp('setup time');
    disp('Standard Deviation Setup time');
    std(tSetup)
    disp('Average Setup time');
    mean(tSetup)
    disp('Minimum Setup time');
    min(tSetup)
    disp('Maximum Setup time');
    max(tSetup)
end

%function RequiredShadowImage = AddNoise(RequiredShadowImage);
function RequiredShadowImage = AddNoise(RequiredShadowImage)
TT = size(RequiredShadowImage);
Err = round(rand(TT(1),TT(2)));
Err(Err == 0) = -1;
RequiredShadowImage = RequiredShadowImage + int32(Err); 

end
%function RecoveredImage = RecoverImage(RequiredShadowImage)
function RecoveredImage = RecoverImage(RequiredShadowImage,Users,Height,Width,base)
TT = size(RequiredShadowImage);
xx = ones(TT(2),TT(2));
for i = 1:TT(2)
   xx(:,i) = xx(:,i).*(Users.^(i-1))';
end;
for i = 1:TT(1)
%    if(mod(i,100)==0)
%        disp(sprintf('%d of %d',i/100,floor(TT(1)/100)));
%    end;
    RecoveredImage(i,:) = solveEq(xx,RequiredShadowImage(i,:),base);
end;
RecoveredImage = reshape(RecoveredImage(:),Height,Width)';
end

%function b = solveEq(Users,RequiredShadowImage(i,:),TT(2),base);
function b = solveEq(xx,yy,base)
b = GetIntMod(inv(sym(xx))*yy',base)';
%b = inv(sym(xx))*yy';
end

%function Re_b = GetIntMod(b,base)
function Re_b = GetIntMod(b,base)
[n,d] = numden(b);
n = double(n);
d = double(d);
Re_b = mod(n.*powermod(d,-1,base),base);
end

%function RequiredShadowImage = GetRequiredShadowImage();
function RequiredShadowImage = GetRequiredShadowImage(Shadow_Image,Users)
TT = length(Users);
for i = 1:TT
    RequiredShadowImage(:,i) = Shadow_Image(:,Users(i));  
end;
end

%ShowShadowImage
function ShowShadowImage(Shadow_Image,Sub_width,Sub_height)
TT = size(Shadow_Image);
for i =1:TT(2)
    figure;
    imshow(uint8(reshape(Shadow_Image(:,i),Sub_width,Sub_height)));
    cmd = sprintf('title(''Shadow Image %d\'')',i);
    eval(cmd);
end;
end

%get Sharing_Polynomial
function Shadow_Image = GetShadow_Image(P_Im,w,base)
TT = int32(size(P_Im));
P_Im = int32(P_Im);
w=int32(w);
base = int32(base);
Shadow_Image = int32(zeros(TT(1),w)); 
for i=1:w
    for j = 1:TT(2)
        Shadow_Image(:,i) = Shadow_Image(:,i) + mod(P_Im(:,j)*i^(j-1), base);
    end;
end;

 Shadow_Image = mod(Shadow_Image,base);
end

%get PreparedIm
function P_Im = GetPreparedIm(Im_linear,t)
if(mod(length(Im_linear),t) ~= 0)
    error('invalid length')
end;
P_Im = reshape(Im_linear,[],t);
end

%Read Image
function [Im_data, Im, hh, w]=ReadImage(Im_Name)
%Im_fp = fopen(Im_Name,'rb');
%Im_data = fread(Im_fp,'uint8');
Im = imread(Im_Name);
[hh, w] = size(Im);
Im_data = Im(:);
%h = figure;   % create a new figure
%Im = uint8(reshape(Im_data,Height,Width)');
%imshow(Im);
%title('Original Image');
end

function y = powermod(a,z,n)
% This function calculates y = a^z mod n
% If a is a matrix, it calculates a(j,k)^z mod for every element in a
[ax,ay]=size(a);


% If a is negative, put it back to between 0 and n-1
a=mod(a,n);

% Take care of any cases where the exponent is negative
if (z<0),
   z=-z;
   for j=1:ax,
      for k=1:ay,
         a(j,k)=invmodn(a(j,k),n);
      end;
   end;   
end;


for j=1:ax,
for k=1:ay,
  x=1;
  a1=a(j,k);
  z1=z;
  while (z1 ~= 0),
     while (mod(z1,2) ==0),
        z1=(z1/2);
        a1=mod((a1*a1), n);
     end;  %end while
     z1=z1-1;
     x=x*a1;
     x=mod(x,n);
  end;
  y(j,k)=x;  
end; %end for k
end; %end for j

end

function y = invmodn( b,n)
% This function calculates the inverse of an element b mod n
% It uses the extended euclidean algorithm

n0=n;
b0=b;
t0=0;
t=1;

q=floor(n0/b0);
r=n0-q*b0;
while r>0,
   temp=t0-q*t;
   if (temp >=0),
      temp=mod(temp,n);
   end;
   if (temp < 0),
      temp= n - ( mod(-temp,n));
   end;
   t0=t;
   t=temp;
   n0=b0;
   b0=r;
   q=floor(n0/b0);
   r=n0-q*b0;
end;

if b0 ~=1,
   y=[];
   disp('No inverse');
else
   y=mod(t,n);
end;   
end