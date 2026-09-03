local window = tonumber(ARGV[1])
local maxRequests = tonumber(ARGV[2])

local current = redis.call('INCR', KEYS[1])

if current == 1 then
	redis.call('EXPIRE', KEYS[1], window)
end

if current > maxRequests then
	return 0
end

return 1