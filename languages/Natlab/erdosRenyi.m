function [t]=erdosRenyi(nv,p,Kreg)
%Funciton [G]=edosRenyi(nv,p,Kreg) generates a random graph based on
%the Erdos and Renyi algoritm where all possible pairs of 'nv' nodes are
%connected with probability 'p'. 
%
% Inputs:
%   nv - number of nodes 
%   p  - rewiring probability
%   Kreg - initial node degree of for regular graph (use 1 or even numbers)
%
% Output:
%   G is a structure inplemented as data structure in this as well as other
%   graph theory algorithms.
%   G.Adj   - is the adjacency matrix (1 for connected nodes, 0 otherwise).
%   G.x and G.y -   are row vectors of size nv wiht the (x,y) coordinates of
%                   each node of G.
%   G.nv    - number of vertices in G
%   G.ne    - number of edges in G
%
%Created by Pablo Blinder. blinderp@bgu.ac.il
%
%Last update 25/01/2005

%build regular lattice 

tic;

A=sparse(nv,nv);
Kreg=fix(abs(Kreg)/2);Kreg=(Kreg<1)+Kreg;

for k=1:Kreg
    A=sparse(A+diag(ones(1,length(diag(A,k))),k)+diag(ones(1,length(diag(A,nv-k))),nv-k));
end
ne0=nnz(A);
%find connected pairs
[v1,v2]=find(A);
% P=permPairs(nv);%my version is faster
Dis=(rand(length(v1),1)<=p);%pairs to disconnect
A(v1(Dis),v2(Dis))=0;
vDis=unique([v1(Dis),v2(Dis)]);%disconnected vertices
nDis=ne0-nnz(A);sum(Dis);

%cycle trough disconnected pairs
disconPairs=[v1(Dis),v2(Dis)];
for n=1:nDis
    %choose one of the vertices from the disconnected pair
    i=ceil(rand*size(disconPairs,1));
    j=logical(1+rand>0.5);
    vDisToRec=disconPairs(i,j);
    %find non adjacent vertices and reconnect
    adj=[find(A(:,vDisToRec)) ; find(A(vDisToRec,:))'];
    nonAdj=setdiff(1:nv,adj);
    vToRec=nonAdj(ceil(rand*length(nonAdj)));
    S=sort([vDisToRec vToRec]);
    A(S(1),S(2))=1;
end
[x,y]=getNodeCoordinates(nv);
%make adjacency matrix symetric
A=A+fliplr((flipud(triu(A))));
G=struct('Adj',A,'x',x','y',y','nv',nv,'ne',nnz(A));

t=0;
t=toc;
%toc

end




function [x,y]=getNodeCoordinates(nv)
%Adapted from circle.m by Zhenhai Wang <zhenhai@ieee.org>. For more details
%see under  MATLAB Central >  File Exchange > Graphics > Specialized
%Plot and Graph Types > Draw a circle.

center=[0,0];
theta=linspace(0,2*pi,nv+1);
rho=ones(1,nv+1);%fit radius and nv
[X,Y] = pol2cart(theta',rho');
X=X+center(1);
Y=Y+center(2);
x=X(1:end-1)*10;
y=Y(1:end-1)*10;

end





function P=permPairs(N)
%Produces all pairs of pairs from 1 to N.
%It is ~30% to 50% faster that nchoosek(1:N,2).
%Created by Pablo 02/12/2003

ini_i=1;
ini_j=ini_i+1;
r0=1;
P=[];
for i=ini_i:N-1
    lj=N-ini_i;
    P(:,r0:lj+r0-1)=[ones(1,lj)*i;ini_j:N];
    r0=r0+lj;
    ini_i=ini_i+1;
    ini_j=ini_i+1;
end
P=P';
end
